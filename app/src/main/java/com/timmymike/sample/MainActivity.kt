package com.timmymike.sample

import android.os.Bundle
import com.timmymike.componenttool.BaseActivity
import com.timmymike.sample.databinding.ActivityMainBinding
import com.timmymike.viewtool.*

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding.tv.apply{
            isClickable = true
            setTextSize(50)
            clickWithTrigger { isSelected = !isSelected }
            setClickTextColorStateById(R.color.black, R.color.purple_700, R.color.teal_200)
            setClickBgStateById(R.color.teal_700)
        }

    }

    override fun observeViewModel() {

    }
}