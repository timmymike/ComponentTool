package com.timmymike.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.timmymike.componenttool.TranslationUtil
import com.timmymike.componenttool.TranslationUtil.setAnimation

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    override fun finish() {
        super.finish()
        this@SecondActivity.setAnimation( TranslationUtil.AnimType.UP_TO_BOTTOM)
    }
}