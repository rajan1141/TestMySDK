package com.test.my.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityFitrofySdpBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import com.test.my.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FitrofySdpActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityFitrofySdpBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityFitrofySdpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            initialiseWebView()
            setUpToolbar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        viewModel.showProgressBar()

        val settings = binding.webViewFitrofySdp.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        //settings.loadWithOverviewMode = true
        //settings.useWideViewPort = true
        binding.webViewFitrofySdp.webViewClient = CustomWebViewClient()
        binding.webViewFitrofySdp.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        viewModel.callFitrofySdpApi()
        viewModel.getFitrofySdp.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.getFitrofyURL.url)) {
                    Utilities.printLogError("FitrofySdpUrl--->${it.data.getFitrofyURL.url}")
                    binding.webViewFitrofySdp.loadUrl(it.data.getFitrofyURL.url!!)
                } else {
                    viewModel.hideProgressBar()
                }
            }
        }
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = "Smart Diet Planner"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.toolBarView.imgHelp.setImageResource(R.drawable.ic_close)
        binding.toolBarView.imgHelp.visibility = View.VISIBLE
        binding.toolBarView.imgHelp.setOnClickListener {
            routeToHomeScreen()
        }
    }

    override fun onBackPressed() {
        if (binding.webViewFitrofySdp.canGoBack()) {
            binding.webViewFitrofySdp.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun routeToHomeScreen() {
        finish()
        //openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    private fun setUpToolbar1() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = "Smart Diet Planner"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            routeToHomeScreen()
        }
    }

}