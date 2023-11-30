package com.timmymike.componenttool

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 * Activity 轉場動畫工具類別
 */
object TranslationUtil {

    /**
     * Activity 轉場動畫類型
     */
    enum class AnimType {
        LEFT_TO_RIGHT,  // A:向左滑出 B:向左滑入
        RIGHT_TO_LEFT,  // A:向右滑出 B:向左滑入
        BOTTOM_TO_UP,
        UP_TO_BOTTOM,
        FADE_IN_OUT,    // 淡入淡出
        ROTATION_IN,        // 旋轉
        ROTATION_BACK,        // 旋轉
    }

    /**
     * 設定 Activity 轉場動畫。
     * @param   context     傳入的 Context 物件
     * @param   animType    轉場動畫類型
     * @return  無
     */
    fun setAnimation(context: Context, animType: AnimType?) {
        val activity: Activity = context as Activity
        when (animType) {
            AnimType.LEFT_TO_RIGHT -> activity.overridePendingTransition(
                R.anim.push_left_in,
                R.anim.push_left_out
            )
            AnimType.RIGHT_TO_LEFT -> activity.overridePendingTransition(
                R.anim.left_to_right,
                R.anim.right_to_left
            )
            AnimType.BOTTOM_TO_UP -> activity.overridePendingTransition(
                R.anim.bottom_to_up,
                R.anim.up_to_bottom
            )
            AnimType.UP_TO_BOTTOM -> activity.overridePendingTransition(
                R.anim.up_to_bottom2,
                R.anim.bottom_to_up2
            )
            AnimType.FADE_IN_OUT -> activity.overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
            AnimType.ROTATION_IN -> activity.overridePendingTransition(
                R.anim.rotate_in_in,
                R.anim.rotate_in_out
            )
            AnimType.ROTATION_BACK -> activity.overridePendingTransition(
                R.anim.rotate_out_in,
                R.anim.rotate_out_out
            )
            else -> {
            }
        }

    }

    fun Int.withDuration(duration: Long): Int {
        val typedValue = TypedValue()
        Resources.getSystem().getValue(this, typedValue, true)
        val originalDuration = typedValue.data
        val newDuration = (originalDuration * duration / 500).toInt() // 500 是默认的 duration
        return TypedValue.complexToDimensionPixelSize(newDuration, Resources.getSystem().displayMetrics)
    }
    private fun getRotateInAnimationId(context:Context,duration: Long): Int {
        return context.resources.getIdentifier("rotate_out_out", "anim", context.packageName).also {
            (context.resources.getAnimation(it) as Animation).apply {
                this@apply.duration = duration // 正確設定 duration
            }
        }
    }
}