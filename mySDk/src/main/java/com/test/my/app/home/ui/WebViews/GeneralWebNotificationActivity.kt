package com.test.my.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
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
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityGeneralWebNotificationBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralWebNotificationActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityGeneralWebNotificationBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityGeneralWebNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            if ( intent.hasExtra(Constants.NOTIFICATION_TITLE) ) {
                setUpToolbar(intent.getStringExtra(Constants.NOTIFICATION_TITLE)!!)
            } else {
                setUpToolbar("")
            }
            initialiseWebView()
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
        binding.webViewGeneralWebNotification.loadUrl(urlToLoad)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings() {
        val settings = binding.webViewGeneralWebNotification.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.webViewGeneralWebNotification.webViewClient = CustomWebViewClient()
        binding.webViewGeneralWebNotification.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Utilities.printLogError("Url--->$url")
            //Utilities.redirectToChrome(url,this@GeneralWebNotificationActivity)
            view.loadUrl(url)
            return true
        }
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
        }
    }

    private fun setUpToolbar(title:String) {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = title
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

    override fun onDestroy() {
        binding.webViewGeneralWebNotification.destroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if( binding.webViewGeneralWebNotification.canGoBack() ) {
            binding.webViewGeneralWebNotification.goBack()
        } else {
            super.onBackPressed()
            routeToHomeScreen()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}