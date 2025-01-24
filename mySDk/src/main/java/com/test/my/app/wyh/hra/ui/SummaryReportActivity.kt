package com.test.my.app.wyh.hra.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.databinding.ActivitySummaryReportBinding
import com.test.my.app.model.wyh.hra.GetHraAnalysisModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.hra.viewmodel.WyhHraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryReportActivity : BaseActivity() {

    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivitySummaryReportBinding
    private val wyhHraViewModel: WyhHraViewModel by lazy {
        ViewModelProvider(this)[WyhHraViewModel::class.java]
    }

    override fun getViewModel(): BaseViewModel = wyhHraViewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //finish()
            routeToHomeScreen()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySummaryReportBinding.inflate(layoutInflater)
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

    private fun initialise() {
        wyhHraViewModel.callGetHraAnalysisApi()
    }

    private fun registerObserver() {
        wyhHraViewModel.getHraAnalysis.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if ( it.data!!.data == null ) {
                    openAnotherActivity(destination = NavigationConstants.WYH_HRA_QUESTIONS_ACTIVITY, clearTop = true)
                } else {
                    setupSummaryReportData(it.data!!.data!!)
                }
            }
        }
    }

    private fun setClickable() {

        binding.nextButton.setOnClickListener {
            //viewModel.clearPreviousQuesDataAndTable()
            openAnotherActivity(destination = NavigationConstants.WYH_HRA_QUESTIONS_ACTIVITY, clearTop = true)
        }

    }

    private fun setupSummaryReportData(data: GetHraAnalysisModel.Data) {

    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolBarHra)
        binding.toolBarView.toolbarTitle.text = "Health Risk Assessment"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolBarHra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolBarHra.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }*/

}