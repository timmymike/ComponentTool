package com.timmymike.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.timmymike.componenttool.TranslationUtil

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    override fun finish() {
        super.finish()
        TranslationUtil.setAnimation(this@SecondActivity, TranslationUtil.AnimType.UP_TO_BOTTOM)
    }
}