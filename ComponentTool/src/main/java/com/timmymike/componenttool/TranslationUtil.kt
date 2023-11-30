package com.timmymike.componenttool

import android.app.Activity

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
        ROTATION_IN,        // 旋轉進入
        ROTATION_BACK,      // 旋轉返回
    }

    /**
     * 設定 Activity 轉場動畫。
     * @param   activity     傳入的 Activity 頁面物件
     * @param   animType    轉場動畫類型
     * @return  無
     */
    fun Activity.setAnimation(animType: AnimType?) {
        when (animType) {
            AnimType.LEFT_TO_RIGHT -> this.overridePendingTransition(
                R.anim.push_left_in,
                R.anim.push_left_out
            )
            AnimType.RIGHT_TO_LEFT -> this.overridePendingTransition(
                R.anim.left_to_right,
                R.anim.right_to_left
            )
            AnimType.BOTTOM_TO_UP -> this.overridePendingTransition(
                R.anim.bottom_to_up,
                R.anim.up_to_bottom
            )
            AnimType.UP_TO_BOTTOM -> this.overridePendingTransition(
                R.anim.up_to_bottom2,
                R.anim.bottom_to_up2
            )
            AnimType.FADE_IN_OUT -> this.overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
            AnimType.ROTATION_IN -> this.overridePendingTransition(
                R.anim.rotate_in_in,
                R.anim.rotate_in_out
            )
            AnimType.ROTATION_BACK -> this.overridePendingTransition(
                R.anim.rotate_out_in,
                R.anim.rotate_out_out
            )
        }
    }
}