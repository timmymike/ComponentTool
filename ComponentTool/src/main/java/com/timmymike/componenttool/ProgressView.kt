package com.timmymike.componenttool

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 載入中對話視窗
 * 提示使用者目前正在進行耗時的工作。
 */
class ProgressView constructor(context: Context) : Dialog(context, android.R.style.Theme_Dialog) {

    private var animDrawable: AnimationDrawable? = null

    init {
        setContentView(ConstraintLayout(context).apply {
            addView(ProgressBar(context))
        })
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    /**
     * 顯示對話框
     */
    fun showLoading() {
        if (!this.isShowing) {
            this.show()
        }
        animDrawable?.start()
    }

    /**
     * 隱藏對話框
     */
    fun hideLoading() {
        if (this.isShowing) {
            this.dismiss()
        }
        animDrawable?.stop()
    }
}