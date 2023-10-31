package com.timmymike.componenttool

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.timmymike.componenttool.databinding.ActivityBaseToolBarBinding
import com.timmymike.viewtool.getScreenHeightPixels
import com.timmymike.viewtool.getScreenWidthPixels
import com.timmymike.viewtool.setHeight
import com.timmymike.viewtool.setRippleBackground
import com.timmymike.viewtool.setSquSize
import com.timmymike.viewtool.setTextSize
import java.lang.reflect.ParameterizedType

/**
 * Activity 基本類別
 * 本類別基於 ViewBinding，將 AppCompatActivity 進行封裝。
 * 除此之外，針對常見需求加入了一些實用方法來減少程式碼重複的情況發生，例如：
 *   頂端工具列的Icon設計、動作
 *   顯示/隱藏虛擬鍵盤
 *   設定/顯示/隱藏載入中畫面
 *   顯示簡易的訊息對話視窗
 *   跳轉Activity等
 *   請求權限
 * 要使用 UI 元件，請使用 binding.<UI元件名稱>。
 * @author Raymond Yang
 * @editor Timmy Hsieh
 */
abstract class BaseToolBarActivity<T : ViewBinding> : AppCompatActivity() {

    val TAG = javaClass.simpleName

    lateinit var binding: T

    lateinit var baseBinding: ActivityBaseToolBarBinding // LinearLayout封裝的Layout

    private var mProgressDialog: ProgressDialog? = null

    private var dialogThemeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityBaseToolBarBinding.inflate(layoutInflater).run {
            baseBinding = this
            tvToolbarTitle.setTextSize(18)
        }

        setContentView(baseBinding.root)

        // 利用反射，呼叫指定 ViewBinding 中的 inflate 方法填充 View
        (javaClass.genericSuperclass as? ParameterizedType)?.let {

            binding = (((it.actualTypeArguments[0] as Class<*>)
                .getMethod("inflate", LayoutInflater::class.java))
                .invoke(null, layoutInflater) as? T) ?: return
        }

        baseBinding.root.addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        supportActionBar?.hide() // 不使用原生的Title，使用自定義的。

        initToolBarIcons()
        setToolbarHeight()
    }

    /**
     * ToolBarIcons 的 IconSize
     * @param ratio  依照螢幕的寬度比例來取得長度pixel值
     * @param iconSize  直接指定icon的pixel值
     */

    open fun initToolBarIcons(ratio: Float = -1f, iconSize: Int = getScreenWidthPixels() / 10) = baseBinding.run {

        val useSize = ratio.takeIf { it != -1f }?.let { getScreenWidthPixels() * it }?.toInt() ?: iconSize
        ivLeftButton.setSquSize( useSize)
        ivRightButton1.setSquSize(useSize)
        ivRightButton2.setSquSize(useSize)

    }

    /**設定左邊Icon：*/
    /**
     * Res：圖片
     * */
    fun setLeftButtonRes(iconRes: Int?) = baseBinding.ivLeftButton.let {
        iconRes?.run {
            it.setImageResource(this)
            it.isVisible = true
        } ?: run { it.isVisible = false }
    }

    /**
     * Ripple：水波紋
     * */
    fun setLeftButtonRipple(colorInt: Int) = baseBinding.ivLeftButton.run {
        setRippleBackground(colorInt)
        isClickable = true
    }

    /**
     * Click：點擊後要執行的動作
     * */
    fun setLeftButtonClick(listener: View.OnClickListener?) = baseBinding.ivLeftButton.setOnClickListener(listener)


    /**設定工具列最右邊的Icon：*/
    /**
     * Res：圖片
     * */
    fun setRightButton1Res(iconRes: Int?) = baseBinding.ivRightButton1.let {
        iconRes?.run {
            it.setImageResource(this)
            it.isVisible = true
        } ?: run { it.isVisible = false }
    }

    /**
     * Ripple：水波紋
     * */
    fun setRightButton1Ripple(colorInt: Int) = baseBinding.ivRightButton1.run {
        setRippleBackground(colorInt)
        isClickable = true
    }

    /**
     * Click：點擊後要執行的動作
     * */
    fun setRightButton1Click(listener: View.OnClickListener?) = baseBinding.ivRightButton1.setOnClickListener(listener)

    /**設定工具列右二的Icon：*/
    /**
     * Res：圖片
     * */
    fun setRightButton2Res(iconRes: Int?) = baseBinding.ivRightButton2.let {
        iconRes?.run {
            it.setImageResource(this)
            it.isVisible = true
        } ?: run { it.isVisible = false }
    }

    /**
     * Ripple：水波紋
     * 註：必須設定點擊後要執行的動作才能有水波紋效果。
     * */
    fun setRightButton2Ripple(colorInt: Int) = baseBinding.ivRightButton2.run {
        setRippleBackground(colorInt)
        isClickable = true
    }

    /**
     * Click：點擊後要執行的動作
     * */
    fun setRightButton2Click(listener: View.OnClickListener?) = baseBinding.ivRightButton2.setOnClickListener(listener)


    /**
     * 設定 Toolbar 的高度
     * @param height  Toolbar 的高度(像素)
     */
    open fun setToolbarHeight(height: Int = getScreenHeightPixels() / 10) {
        baseBinding.clToolBar.setHeight(height)
    }


    /**
     * 是否顯示 Toolbar
     * @param isVisible  是否顯示。true=顯示、false=不顯示
     */
    fun setToolbarVisible(isVisible: Boolean) {
        baseBinding.clToolBar.isVisible = isVisible
    }

    /**
     * 設定 Toolbar 的背景圖片
     * @param resId  Toolbar 的背景圖片資源
     */
    fun setToolbarBackgroundById(resId: Int) {
        baseBinding.clToolBar.setBackgroundResource(resId)
    }

    /**
     * 設定 Toolbar 的標題圖片
     * @param resId  Toolbar 的標題圖片資源
     */
    fun setToolbarTitleImgById(resId: Int) {
        baseBinding.ivToolbarTitle.run {
            setImageResource(resId)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    /**
     * 設定 Toolbar 的背景色
     * @param colorInt  Toolbar 的色
     */
    fun setToolbarBackgroundColor(@ColorInt colorInt: Int) {
        baseBinding.clToolBar.setBackgroundColor(colorInt)
    }

    /**
     * Android版本大於等於21才有效。
     * 設定 Toolbar 的高度，若需顯示 Toolbar 的陰影，請將高度設定高於0
     * @param elevation  Toolbar 的高度
     */
    fun setToolbarElevation(elevation: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            baseBinding.clToolBar.elevation = elevation
        }
    }

    /**
     * 設定 Toolbar 的文字色彩
     * @param colorInt  Toolbar 的文字色彩
     */
    fun setToolbarTextColor(@ColorInt colorInt: Int) {
        baseBinding.tvToolbarTitle.setTextColor(colorInt)
    }

    override fun setTitle(title: CharSequence?) {
        baseBinding.tvToolbarTitle.text = title
    }

    override fun setTitle(titleId: Int) {
        baseBinding.tvToolbarTitle.text = getString(titleId)
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
     * 使用範例：
     * 使用Lottie來顯示動畫
    setDialogLoading(view = com.airbnb.lottie.LottieAnimationView(this@MainActivity).apply {
    // 異步載入
    LottieCompositionFactory.fromAsset(context, "loading_1.json").addListener { result ->
    setComposition(result)
    }
    // 同步載入
    setComposition(LottieCompositionFactory.fromAssetSync(context, "loading_1.json").value ?: return@apply)
    repeatCount = LottieDrawable.INFINITE
    repeatMode = LottieDrawable.RESTART
    playAnimation()
    })
     *
     * 或
     * 新增一個不一樣的ProgressView蓋上去
    setDialogLoading(view = ProgressBar(this@MainActivity).apply {
    indeterminateDrawable.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    setSquSize(200.dpToPx)
    })
     *
     * 或
     * 原生動畫直接設定顏色
    setDialogLoading(Color.GRAY) // 設定灰色
     *
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
    fun gotoActivityAndClearStack(intent: Intent, bundle: Bundle?) {
        if (bundle != null) intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
    }

}
