package com.test.my.app.home.ui.nimeya

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
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityNimeyaWebViewBinding
import com.test.my.app.home.viewmodel.NimeyaViewModel
import com.test.my.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NimeyaWebViewActivity : BaseActivity() {

    private val viewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }
    private lateinit var binding: ActivityNimeyaWebViewBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityNimeyaWebViewBinding.inflate(layoutInflater)
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

        val settings = binding.webViewNimeya.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.webViewNimeya.webViewClient = CustomWebViewClient()
        binding.webViewNimeya.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

        viewModel.callGetNimeyaUrlApi()
        viewModel.getNimeyaUrl.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.url)) {
                    Utilities.printLogError("NimeyaUrl--->${it.data.url}")
                    binding.webViewNimeya.loadUrl(it.data.url)
                } else {
                    viewModel.hideProgressBar()
                }
            }
        }
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            //view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = "Financial Articles"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (binding.webViewNimeya.canGoBack()) {
            binding.webViewNimeya.goBack()
        } else {
            super.onBackPressed()
            routeToHomeScreen()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}