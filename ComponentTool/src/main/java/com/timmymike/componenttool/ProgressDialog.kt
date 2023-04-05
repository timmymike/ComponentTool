package com.timmymike.componenttool

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

/**
 * 載入中對話視窗
 * 提示使用者目前正在進行耗時的工作。
 */
class ProgressDialog constructor(context: Context, pgColor: Int? = null, progressView: View? = null) : Dialog(context, android.R.style.Theme_Dialog) {

    init {

        // 設定對話框底色為透明
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(
            ConstraintLayout(context).apply {
                addView(
                    progressView ?: (ProgressBar(context).apply {// 若progressView未設定：
                        pgColor?.let {
                            indeterminateDrawable.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                        }
                        // 長寬設置為螢幕寬度的 1/5
                        (Resources.getSystem().displayMetrics.widthPixels / 5).let { dimen ->
                            layoutParams = ConstraintLayout.LayoutParams(dimen, dimen) // 正方形。
                        }
                    })
                )
            })
        setCancelable(true)
        setCanceledOnTouchOutside(false)

    }

    /**
     * 顯示對話框
     */
    fun showLoading() {
        if (!this.isShowing) {
            this.show()
        }
    }

    /**
     * 隱藏對話框
     */
    fun hideLoading() {
        if (this.isShowing) {
            this.dismiss()
        }
    }
}