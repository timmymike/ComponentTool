package com.timmymike.sample

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.timmymike.componenttool.BaseToolBarActivity
import com.timmymike.componenttool.TranslationUtil
import com.timmymike.componenttool.ViewBindingAdapter
import com.timmymike.sample.databinding.ActivityMainBinding
import com.timmymike.sample.databinding.AdapterSampleBinding
import com.timmymike.viewtool.*

class MainActivity : BaseToolBarActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_main)

        initToolBar()

        initViewComponent()

        initRecyclerView()

        initLoadingView()

    }

    private fun initToolBar() {
        setToolbarElevation(14f)
        setToolbarTextColor(getResourceColor(R.color.purple_700))
        initToolBarIcons(0.1f)
        setToolbarHeight(getScreenHeightPixels() / 10)
        //        title = "19199"
        setToolbarTitleImgById(R.drawable.title_sample)

        setLeftButtonRes(R.drawable.baseline_message_24)
        setLeftButtonRipple(Color.RED)
        //        setLeftButtonClick{
        //            Log.e("ggg","aaa")
        //        }

        setRightButton1Res(R.drawable.baseline_message_24)
        setRightButton1Ripple(Color.BLUE)
        //        setRightButton1Click{
        //            Log.e("ggg","bbb")
        //        }

        setRightButton2Res(R.drawable.baseline_message_24)
        setRightButton2Ripple(Color.GREEN)
        //        setRightButton2Click{
        //            Log.e("ggg","ccc")
        //        }
        //
    }

    private fun initViewComponent() {
        binding.tv.apply {
            isClickable = true
            setTextSize(50)
            clickWithTrigger { isSelected = !isSelected }
            setClickTextColorStateById(R.color.black, R.color.purple_700)
            setClickBgStateById(R.color.teal_700, android.R.color.holo_purple)
            setRippleBackground(R.color.black)
        }
    }

    private fun initLoadingView() {
        /**設定一個LottieView蓋上去*/
        setDialogLoading(view = LottieAnimationView(this@MainActivity).apply {
            // 異步載入
            LottieCompositionFactory.fromAsset(context, "loading_1.json").addListener { result ->
                setComposition(result)
            }

            // 同步載入
    //            setComposition(LottieCompositionFactory.fromAssetSync(context, "loading_1.json").value ?: return@apply)

            repeatCount = LottieDrawable.INFINITE
            repeatMode = LottieDrawable.RESTART
            playAnimation()
            setViewSize(150.dpToPx, 150.dpToPx)
        })


        /**新增一個不一樣的ProgressView蓋上去*/
//        setDialogLoading(view = ProgressBar(this@MainActivity).apply {
//            indeterminateDrawable.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
//            setViewSize(200.dpToPx, 200.dpToPx)
//        })

        /**只將原有的ProgressView設定不同的顏色*/
//        setDialogLoading(color = Color.GREEN)

//        showDialogLoading()

//        MainScope().launch {
//            showDialogLoading()
//            delay(3000L)
//            hideDialogLoading()
//            delay(3000L)
//            showDialogLoading()
//        }

    }

    private fun initRecyclerView() {
        val colorList = mutableListOf("#5e5ce6", "#8b00ff", "#c80080", "#d25f52", "#2eb8b8", "#a80000", "#ff6600", "#800000", "#b200b2", "#9932cc", "#006400", "#00ffff", "#d2691e", "#7f7f7f", "#7cfc00", "#ff00ff", "#f08080", "#ffa500", "#00fa9a", "#1e90ff")
        val selectedList = MutableList(colorList.size) { false }
        binding.vp2.adapter = ViewBindingAdapter.create<AdapterSampleBinding, String>(AdapterSampleBinding::inflate) { it ->
            tvSample.text = it
            ivSample.clickWithTrigger { v ->
//                v?.isSelected = v?.isSelected?.not() ?: false
//                selectedList[colorList.indexOf(it)] = v?.isSelected ?: false
                gotoActivity(Intent(this@MainActivity,SecondActivity::class.java))
                TranslationUtil.setAnimation(this@MainActivity, TranslationUtil.AnimType.BOTTOM_TO_UP)
            }

            ivSample.setClickBgState(
                ColorDrawable(Color.parseColor(it)),
                ColorDrawable(Color.parseColor("#708090")),
                ColorDrawable(Color.parseColor("#00ff7f")),
            )

            ivSample.setRippleBackground(Color.parseColor(it))

        }.apply {
            viewHolderInitialCallback = { it -> // 第一次產生
                it.binding.root.resetLayoutTextSize()
            }
            viewAttachedToWindowCallback = { it, position -> // 每一次更新畫面
                it.binding.ivSample.isSelected = selectedList[position]

            }
            submitList(colorList)
        }
    }

}