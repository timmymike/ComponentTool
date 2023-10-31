package com.timmymike.componenttool

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Activity 基本類別
 * 本類別基於 ViewBinding，將 AppCompatActivity 進行封裝。
 * 除此之外，針對常見需求加入了一些實用方法來減少程式碼重複的情況發生，例如：
 *   顯示/隱藏虛擬鍵盤
 *   設定/顯示/隱藏載入中畫面
 *   顯示簡易的訊息對話視窗
 *   跳轉Activity等
 *   請求權限
 * 要使用 UI 元件，請使用 binding.<UI元件名稱>。
 * @author Raymond Yang
 * @editor Timmy Hsieh
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    val TAG = javaClass.simpleName

    lateinit var binding: T

    private var mProgressDialog: ProgressDialog? = null

    private var dialogThemeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 利用反射，呼叫指定 ViewBinding 中的 inflate 方法填充 View
        (javaClass.genericSuperclass as? ParameterizedType)?.let {

            binding = (((it.actualTypeArguments[0] as Class<*>)
                .getMethod("inflate", LayoutInflater::class.java))
                .invoke(null, layoutInflater) as? T) ?: return
        }
        setContentView(binding.root)

    }

    /**
     * 顯示虛擬鍵盤
     * @param view  鍵盤的焦點
     */
    fun showKeyboard(view: EditText?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        view?.isCursorVisible = true
    }

    /**
     * 隱藏虛擬鍵盤
     * @param view
     */
    fun hideKeyboard(view: View) {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 隱藏虛擬鍵盤
     * 使用本Activity作參數傳入
     */
    fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.window.decorView.clearFocus()
        imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
    }

    /**
     * 設定載入畫面
     * @param color 設定顏色
     * @param view 設定替代的View(例如可以使用LottieView)
     * @author timmy
     * ※color和view同時傳入時，只有view的設定會有效。
     * ※設定多次，只有最後一次的設定會有效。
     */
    fun setDialogLoading(color: Int? = null, view: View? = null) {
        mProgressDialog = ProgressDialog(this, color, view)
    }

    /**
     * 顯示載入畫面
     */
    fun showDialogLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
        }
        mProgressDialog?.showLoading()
    }

    /**
     * 隱藏載入畫面
     */
    fun hideDialogLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog?.hideLoading()
        }
    }

    /**
     * checkAndRequestPermissions
     * 只能、也必須在[onResume]呼叫。
     * 因為需要利用[onResume]回call來執行動作。
     * */
    fun checkAndRequestPermissions(stackPermissions: Array<String>, actionWhenGranted: (() -> Unit)? = null, actionWhenDenied: (() -> Unit)? = null) {
        // 檢查是否需要解釋權限請求
        if (stackPermissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
            actionWhenDenied?.invoke() // 顯示權限遭拒對話框
        } else if (stackPermissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            actionWhenGranted?.invoke() // 權限允許執行的動作
        } else
        // 發起權限請求
            ActivityCompat.requestPermissions(this, stackPermissions, 107)
    }

    /**
     * 自定義對話框風格
     * @param themeId 資源檔的風格，R.style.XXX
     * */
    fun setMessageDialogTheme(@StyleRes themeId: Int) {
        this.dialogThemeId = themeId
    }

    /**
     * 顯示訊息對話框
     * @param msg  字串 或 資源檔內容
     * @param onPositivePress  按下確定時的動作，預設 null=按下後關閉
     * @param onNegativePress  按下取消時的動作，預設 null=不顯示
     */
    fun showMessageDialog(
        msg: Any,
        onPositivePress: (() -> Unit)? = null,
        onNegativePress: (() -> Unit)? = null
    ) {
        (dialogThemeId?.let { AlertDialog.Builder(this, it) } ?: run { AlertDialog.Builder(this) }).apply {
            setMessage(
                when (msg) {
                    is String -> msg
                    is Int -> getString(msg)
                    else -> msg.toString()
                }
            )
            setPositiveButton(android.R.string.ok) { thisDlg, _ ->
                onPositivePress?.invoke()
                thisDlg.dismiss()
            }
            if (onNegativePress != null) {
                setNegativeButton(android.R.string.cancel) { thisDlg, _ ->
                    onNegativePress.invoke()
                    thisDlg.dismiss()
                }
            }
            show()
        }
    }

    /**
     * 開啟外部預覽程式
     * @param uri   目標 Uri 字串
     * @param closeSelf  跳轉後是否關閉自己
     */
    fun gotoActivity(uri: String, closeSelf: Boolean = false) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
        if (closeSelf) {
            finish()
        }
    }

    /**
     * 跳轉到其他 Activity
     * @param activity   目標 Activity
     * @param bundle     傳遞的資料
     * @param closeSelf  跳轉後是否關閉自己
     */
    fun <A> gotoActivity(activity: Class<A>, bundle: Bundle? = null, closeSelf: Boolean = false) {
        val intent = Intent(this, activity)
        if (bundle != null) intent.putExtras(bundle)
        startActivity(intent)
        if (closeSelf) {
            finish()
        }
    }

    /**
     * 跳轉到其他 Activity
     * @param intent     整理完的intent
     * @param bundle     傳遞的資料
     * @param closeSelf  跳轉後是否關閉自己
     */
    fun gotoActivity(intent: Intent, bundle: Bundle? = null, closeSelf: Boolean = false) {
        if (bundle != null) intent.putExtras(bundle)
        startActivity(intent)
        if (closeSelf) {
            finish()
        }
    }

    /**
     * 跳轉到其他 Activity，並將所有的 Activity 從任務堆疊中移除
     * @param activity   目標 Activity
     * @param bundle     傳遞的資料
     */
    fun <A> gotoActivityAndClearStack(activity: Class<A>, bundle: Bundle? = null) {
        val intent = Intent(this, activity)
        if (bundle != null) intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
    }

    /**
     * 跳轉到其他 Activity，並將所有的 Activity 從任務堆疊中移除
     * @param intent  整理完的intent
     * @param bundle  傳遞的資料
     */
    fun <A> gotoActivityAndClearStack(intent: Intent, bundle: Bundle?) {
        if (bundle != null) intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
    }

}
