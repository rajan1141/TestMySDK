package com.test.my.app.security.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityAppIntroductionBinding
import com.test.my.app.model.home.LanguageModel
import com.test.my.app.security.ui_dialog.DialogLanguage
import com.test.my.app.security.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class AppIntroductionActivity : BaseActivity(), DialogLanguage.OnLanguageClickListener {

    private val viewModel: StartupViewModel by lazy {
        ViewModelProvider(this)[StartupViewModel::class.java]
    }
    private lateinit var binding: ActivityAppIntroductionBinding

    private var myViewPagerAdapter: AppIntroViewPagerAdapter? = null
    private var slidingImageDots: Array<ImageView?> = arrayOf()
    private var slidingDotsCount = 0
    private var currentPage = 0
    private lateinit var layouts: IntArray
    private val timer = Timer()
    private var execute = true
    private var dialogLanguage: DialogLanguage? = null

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("ResourceType")
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityAppIntroductionBinding.inflate(layoutInflater)
        updateStatusBarColor(R.drawable.gradient_intro, false)
        setContentView(binding.root)
        initialise()
        setClickable()
    }

    private fun initialise() {
        addCleverTapEvent(0)
        // add few more layouts if you want
        layouts = intArrayOf(
            R.layout.intro_app_slide_1,
            R.layout.intro_app_slide_2,
            R.layout.intro_app_slide_3,
            R.layout.intro_app_slide_4)
        slidingDotsCount = layouts.size
        //myViewPagerAdapter = AppIntroViewPagerAdapter(this, layouts)
        //binding.slidingViewPager.adapter = myViewPagerAdapter
        setUpSlidingViewPager()
    }

    private fun setClickable() {

        if (LocaleHelper.getLanguage(this) == "hi") {
            binding.toolBarView.txtLanguage.text = resources.getString(R.string.HINDI)
        } else {
            binding.toolBarView.txtLanguage.text = resources.getString(R.string.ENGLISH)
        }

        binding.toolBarView.tabLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.btnSkip.setOnClickListener {
            CleverTapHelper.pushEventWithoutProperties(this,CleverTapConstants.APP_INTRODUCTION_SKIP)
            navigateToLogin()
        }

        binding.slidingViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = getCurrentScreen()
                //Utilities.printLogError("SelectedPagePos--->$position")
                addCleverTapEvent(position)
                for (i in 0 until slidingDotsCount) {
                    slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_non_active))
                }
                slidingImageDots[position]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_active))
                if (position == (layouts.size - 1)) {
                    binding.btnSkip.visibility = View.GONE
                    binding.btnNext.text = resources.getString(R.string.FINISH)
                } else {
                    binding.btnSkip.visibility = View.VISIBLE
                    binding.btnNext.text = resources.getString(R.string.NEXT)
                }
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.btnNext.setOnClickListener {
            when (getCurrentScreen()) {
                (layouts.size - 2) -> {
                    scrollNextSlide()
                    //binding.btnNext.text = resources.getString(R.string.TAKE_ME_TO_APP)
                }

                (layouts.size - 1) -> {
                    navigateToLogin()
                }

                else -> {
                    scrollNextSlide()
                    //binding.btnNext.text = resources.getString(R.string.NEXT)
                }
            }
            currentPage = getCurrentScreen()
        }

    }

    fun addCleverTapEvent(pos:Int) {
        Utilities.printLogError("PageNumber--->${pos+1}")
        val data = HashMap<String,Any>()
        data[CleverTapConstants.PAGE_NUMBER] = pos+1
        CleverTapHelper.pushEventWithProperties(this,CleverTapConstants.APP_INTRODUCTION_SCREEN,data,false)
    }

    private fun navigateToLogin() {
        viewModel.setFirstTimeUserFlag(false)
        timer.cancel()
        if (Utilities.getBooleanPreference(PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE)) {
            openAnotherActivity(destination = NavigationConstants.SPLASH_SCREEN, clearTop = true)
        } else {
//            openAnotherActivity(destination = NavigationConstants.LOGIN)
            finish()
        }
    }

    private fun showLanguageSelectionDialog() {
        dialogLanguage = DialogLanguage(this, this)
        dialogLanguage!!.show()
    }

    private fun getCurrentScreen(): Int {
        return binding.slidingViewPager.currentItem
    }

    private fun scrollNextSlide() {
        Utilities.printLogError("setCurrentItem--->${getCurrentScreen() + 1}")
        binding.slidingViewPager.setCurrentItem(getCurrentScreen() + 1, true)
    }

    private fun addBottomDots() {
        slidingImageDots = arrayOfNulls(slidingDotsCount)
        binding.layoutDots.removeAllViews()
        for (i in 0 until slidingDotsCount) {
            slidingImageDots[i] = ImageView(this)
            slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_non_active))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 0, 8, 0)
            binding.layoutDots.addView(slidingImageDots[i], params)
        }
        slidingImageDots[0]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))
    }

    private fun getItem(i: Int): Int {
        return binding.slidingViewPager.currentItem + i
    }

    private fun setUpSlidingViewPager() {
        try {
            slidingImageDots = arrayOfNulls(slidingDotsCount)
            myViewPagerAdapter = AppIntroViewPagerAdapter(this,layouts)
            binding.slidingViewPager.adapter = myViewPagerAdapter
            binding.layoutDots.visibility = View.VISIBLE
            addBottomDots()
            val handler = Handler(Looper.getMainLooper())
            val update = Runnable {
                Utilities.printLogError("currentPage--->$currentPage")
                if (currentPage == slidingDotsCount) {
                    currentPage = 0
                    timer.cancel()
                    execute = false
                    Utilities.printLogError("Timer Canceled")
                }
                if ( execute ) {
                    binding.slidingViewPager.setCurrentItem(currentPage++, true)
                }
                Utilities.printLogError("currentPage--->$currentPage")
            }
            timer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(update)
                }
            },0,4000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * View pager adapter
     */
    class AppIntroViewPagerAdapter(val context: Context, private val layouts: IntArray) :
        PagerAdapter() {
        //private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            layoutInflater = LayoutInflater.from(context)
            val view: View = LayoutInflater.from(context).inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    override fun onLanguageSelection(data: LanguageModel) {
        Utilities.printData("LanguageModel", data, true)
        Utilities.changeLanguage(data.languageCode, this@AppIntroductionActivity)
//        Utilities.logCleverTapChangeLanguage(data.languageCode, this@AppIntroductionActivity)
        recreate()
        binding.toolBarView.txtLanguage.text = Utilities.getLanguageNameConverted(data.languageCode, this@AppIntroductionActivity)
    }

}