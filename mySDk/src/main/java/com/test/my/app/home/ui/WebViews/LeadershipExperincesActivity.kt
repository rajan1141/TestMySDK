package com.test.my.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.R
import com.test.my.app.databinding.ActivityLeadershipExperincesBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeadershipExperincesActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityLeadershipExperincesBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityLeadershipExperincesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            initialiseWebView()
            setUpToolbar()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        viewModel.showProgress()
        setWebViewSettings()
        val urlToLoad = intent.getStringExtra(Constants.WEB_URL)!!
        Utilities.printLogError("UrlToLoad--->$urlToLoad")
        binding.webViewLeadershipExperinces.loadUrl(urlToLoad)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings() {
        val settings = binding.webViewLeadershipExperinces.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.webViewLeadershipExperinces.webViewClient = CustomWebViewClient()
        binding.webViewLeadershipExperinces.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView,url: String): Boolean {
            Utilities.printLogError("Url--->$url")
            //Utilities.redirectToChrome(url,this@LeadershipExperincesActivity)
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
        binding.toolBarView.toolbarTitle.text = "Leadership Experiences"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if( binding.webViewLeadershipExperinces.canGoBack() ) {
            binding.webViewLeadershipExperinces.goBack()
        } else {
            super.onBackPressed()
            routeToHomeScreen()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}