package com.test.my.app.wyh.ira.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityIraSummaryReportBinding
import com.test.my.app.model.wyh.ira.GetIraHistoryModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.ira.viewmodel.WyhIraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IraSummaryReportActivity : BaseActivity() {

    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityIraSummaryReportBinding
    private val wyhHraViewModel: WyhIraViewModel by lazy {
        ViewModelProvider(this)[WyhIraViewModel::class.java]
    }

    override fun getViewModel(): BaseViewModel = wyhHraViewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //finish()
            routeToHomeScreen()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityIraSummaryReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
            setUpToolbar()
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        binding.layoutMain.visibility = View.GONE
        wyhHraViewModel.callGetIraHistoryApi()
        binding.indicatorScore.isClickable = false
        binding.indicatorScore.setOnTouchListener { _: View?, _: MotionEvent? -> true }
    }

    private fun registerObserver() {
        wyhHraViewModel.getIraHistory.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( it.data!!.data != null && it.data.data!!.data!!.isNotEmpty() ) {
                    binding.layoutMain.visibility = View.VISIBLE
                    setupSummaryReportData(it.data.data.data!![0])
                } else {
                    wyhHraViewModel.callIraCreateConversationApi()
                }
            }
        }

        wyhHraViewModel.iraCreateConversation.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( it.data!!.data != null && !Utilities.isNullOrEmpty(it.data.data!!.conversationId) ) {
                    openAnotherActivity(destination = NavigationConstants.WYH_IRA_QUESTIONS_ACTIVITY, clearTop = true) {
                        putString(Constants.CONVERSATION_ID, it.data.data.conversationId)
                    }
                }
            }
        }
    }

    private fun setClickable() {

        binding.btnRetest.setOnClickListener {
            wyhHraViewModel.callIraCreateConversationApi(true)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setupSummaryReportData(data: GetIraHistoryModel.DataResp) {
        binding.txtScore.text = "${data.score}"
        binding.txtScoreTotal.text = "/${data.questionsScore}"
        binding.indicatorScore.setValueAnimated(data.score!!.toFloat(),Constants.ANIMATION_DURATION.toLong())

        binding.txtAssessmentObs.text = Html.fromHtml(data.content, Html.FROM_HTML_MODE_LEGACY)

        binding.txtLastAssessmentDate.text = "${resources.getString(R.string.LAST_UPDATED)} : ${DateHelper.convertDateSourceToDestination(data.createdAt,DateHelper.DATE_FORMAT_UTC,DateHelper.DATE_TIME_FORMAT_NEW)}"
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolBarHra)
        binding.toolBarView.toolbarTitle.text = "Immunity Risk Summary"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolBarHra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.imgCancel.visibility = View.GONE
        binding.toolBarView.toolBarHra.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

}