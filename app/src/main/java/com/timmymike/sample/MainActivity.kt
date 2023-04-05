package com.timmymike.sample

import android.graphics.Color
import android.os.Bundle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.OnCompositionLoadedListener
import com.timmymike.componenttool.BaseActivity
import com.timmymike.sample.databinding.ActivityMainBinding
import com.timmymike.viewtool.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding.tv.apply {
            isClickable = true
            setTextSize(50)
            clickWithTrigger { isSelected = !isSelected }
            setClickTextColorStateById(R.color.black, R.color.purple_700)
            setClickBgStateById(R.color.teal_700, android.R.color.holo_purple)
            setRippleBackground(R.color.black)
        }

        /**設定一個LottieView蓋上去*/
        setDialogLoading(view = com.airbnb.lottie.LottieAnimationView(this@MainActivity).apply {
            // 異步載入
            LottieCompositionFactory.fromAsset(context, "loading_1.json").addListener {
                    result ->
                setComposition(result)
            }

            // 同步載入
//            setComposition(LottieCompositionFactory.fromAssetSync(context, "loading_1.json").value ?: return@apply)

            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
            setViewSize(150.dpToPx, 150.dpToPx)
        })
//
        /**新增一個不一樣的ProgressView蓋上去*/
//        setDialogLoading(view = ProgressBar(this@MainActivity).apply {
//            indeterminateDrawable.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
//            setViewSize(200.dpToPx, 200.dpToPx)
//        })

        /**只將原有的ProgressView設定不同的顏色*/
//        setDialogLoading(color = Color.GREEN)

        MainScope().launch {

            showDialogLoading()
            delay(3000L)
            hideDialogLoading()
            delay(3000L)
            showDialogLoading()
        }
    }

}