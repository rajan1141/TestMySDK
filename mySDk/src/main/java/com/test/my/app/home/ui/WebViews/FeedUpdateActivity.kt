package com.test.my.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
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
import com.test.my.app.databinding.ActivityFeedUpdateBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedUpdateActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityFeedUpdateBinding
    private val appColorHelper = AppColorHelper.instance!!

    private var urlToLoad = ""
    private var title = ""
    private var from = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityFeedUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            if (intent.hasExtra(Constants.FROM)) {
                from = intent.getStringExtra(Constants.FROM)!!
            }
            if (intent.hasExtra(Constants.TITLE)) {
                title = intent.getStringExtra(Constants.TITLE)!!
            }
            if (intent.hasExtra(Constants.WEB_URL)) {
                urlToLoad = intent.getStringExtra(Constants.WEB_URL)!!
            }
            Utilities.printLogError("From--->$from")
            Utilities.printLogError("Title--->$title")
            Utilities.printLogError("UrlToLoad--->$urlToLoad")
            setUpToolbar(title)
            initialiseWebView()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        viewModel.showProgress()
        setWebViewSettings()
        when(from) {
            Constants.WEB_HTML -> {
                if (intent.hasExtra(Constants.WEB_HTML)) {
                    urlToLoad = intent.getStringExtra(Constants.WEB_HTML)!!
                }
                binding.webViewFeed.loadDataWithBaseURL(null,urlToLoad,"text/html","UTF-8",null)
            }
            else -> {
                binding.webViewFeed.loadUrl(urlToLoad)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings() {
        val settings = binding.webViewFeed.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.webViewFeed.webViewClient = CustomWebViewClient()
        binding.webViewFeed.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))

        binding.webViewFeed.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
    }

    inner class CustomWebViewClient : WebViewClient() {

/*        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Utilities.printLogError("Url--->$url")
            //Utilities.redirectToChrome(url,this@FeedUpdateActivity)
            view.loadUrl(url)
            return true
        }*/

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url.toString()
            Utilities.printLogError("Url--->$url")
            // Check if the URL is a Google Maps URL
            if ( url.contains("/maps") ) {
                Utilities.printLogError("FoundGoogleMapUrl--->$url")
                // Launch the Google Maps app with the URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.setPackage("com.google.android.apps.maps")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // Fallback: Open in browser if Google Maps is not installed
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                return true // Indicate that we've handled the URL
            }
            // Default behavior: Load the URL in the WebView
            view?.loadUrl(url)
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
        binding.toolBarView.imgHelp.setImageResource(R.drawable.ic_close)
        binding.toolBarView.imgHelp.visibility = View.VISIBLE
        binding.toolBarView.imgHelp.setOnClickListener {
            when(from) {
                Constants.NOTIFICATION -> routeToHomeScreen()
                else-> finish()
            }
        }
    }

    override fun onDestroy() {
        binding.webViewFeed.destroy()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if( binding.webViewFeed.canGoBack() ) {
            binding.webViewFeed.goBack()
        } else {
            super.onBackPressed()
            when(from) {
                "POLICY",Constants.WEB_HTML -> finish()
                else-> routeToHomeScreen()
            }
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    /*    override fun onBackPressed() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }*/


}