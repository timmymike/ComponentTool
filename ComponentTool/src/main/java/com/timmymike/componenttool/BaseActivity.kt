package com.timmymike.componenttool

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Activity 基本類別
 * 本類別基於 ViewBinding，將 AppCompatActivity 進行封裝。
 * 並且客製化一個標題置中的 Toolbar，以及實作了 Toolbar 的一些設定方法。
 * 除此之外，針對常見需求加入了一些實用方法來減少程式碼重複的情況發生，例如：
 *   顯示/隱藏虛擬鍵盤
 *   顯示/隱藏載入中畫面
 *   顯示簡易的訊息對話視窗
 *   跳轉Activity等
 * 要使用 UI 元件，請使用 binding.<UI元件名稱>。
 * @author Raymond Yang
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    val TAG = javaClass.simpleName

    lateinit var binding: T

    private var mProgressView: ProgressView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 利用反射，呼叫指定 ViewBinding 中的 inflate 方法填充 View
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<T>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as T
        }
        setContentView(binding.root)

        observeViewModel()
    }

    abstract fun observeViewModel()
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
     * @param activity
     */
    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.window.decorView.clearFocus()
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    /**
     * 顯示載入畫面
     */
    fun showDialogLoading() {
        if (mProgressView == null) {
            mProgressView = ProgressView(this)
        }
        mProgressView?.showLoading()
    }

    /**
     * 隱藏載入畫面
     */
    fun hideDialogLoading() {
        if (mProgressView != null && mProgressView!!.isShowing) {
            mProgressView?.hideLoading()
        }
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
        AlertDialog.Builder(this).apply {
            setMessage(
                when (msg) {
                    is String -> msg
                    is Int -> getString(msg)
                    else -> msg.toString()
                }
            )
            setPositiveButton(android.R.string.ok) { thisDlg, which ->
                onPositivePress?.invoke()
                thisDlg.dismiss()
            }
            if (onNegativePress != null) {
                setNegativeButton(android.R.string.cancel) { thisDlg, which ->
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

}