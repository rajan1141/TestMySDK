package com.test.my.app.home.ui.WebViews

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import org.json.JSONObject
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivitySaltWebViewBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaltWebViewActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivitySaltWebViewBinding
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySaltWebViewBinding.inflate(layoutInflater)
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

        binding.webViewSalt.settings.javaScriptEnabled = true
        binding.webViewSalt.addJavascriptInterface(JSBridge(this),"JSBridge")
        binding.webViewSalt.webViewClient = CustomWebViewClient()

        val mptResultPageUrl = Utilities.getUserPreference(PreferenceConstants.MPT_RESULT_PAGE_URL)
        if ( !Utilities.isNullOrEmpty(mptResultPageUrl) ) {
            Utilities.printLogError("UrlToLoad--->$mptResultPageUrl")
            binding.webViewSalt.loadUrl(mptResultPageUrl)
        } else {
            binding.webViewSalt.clearCache(true)
            binding.webViewSalt.clearFormData()
            binding.webViewSalt.clearHistory()
            binding.webViewSalt.clearSslPreferences()
            val mptUrl = "${Constants.SALT_MPT_API}?phone=${viewModel.phone}"
            Utilities.printLogError("UrlToLoad--->$mptUrl")
            binding.webViewSalt.loadUrl(mptUrl)
        }
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            viewModel.hideProgressBar()
        }

    }

    /**
     * Receive message from webView and pass on to native.
     */
    class JSBridge(val context: Context) {
        @JavascriptInterface
        fun showMessageInNative(resultData:String) {
            try {
                val data = JSONObject(resultData)
                Utilities.storeUserPreference(PreferenceConstants.MPT_RESULT_ID,data.getString("resultId"))
                Utilities.storeUserPreference(PreferenceConstants.MPT_RESULT_PAGE_URL,data.getString("resultPageUrl"))
                Utilities.storeUserPreference(PreferenceConstants.MPT_COHORT_TITLE,data.getString("cohortTitle"))
                Utilities.storeUserPreference(PreferenceConstants.MPT_COHORT_ICON_URL,data.getString("cohortIconUrl"))
                val cleverTapData = HashMap<String, Any>()
                cleverTapData[CleverTapConstants.FROM] = CleverTapConstants.VIEW_RESULT
                CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.SALT_MPT,cleverTapData)

                val saltData = HashMap<String, Any>()
                saltData[CleverTapConstants.VALUE] = data.getString("cohortTitle")
                CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.SALT_MPT_INFO,saltData)
            } catch ( e:Exception ) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.MONEY_PERSONALITY_TEST)
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
        super.onBackPressed()
        routeToHomeScreen()
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}