package com.test.my.app.home.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.common.utils.PermissionUtil
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityAvatarWebViewBinding
import com.test.my.app.home.common.WebViewInterface
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AvatarWebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityAvatarWebViewBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val appColorHelper = AppColorHelper.instance!!
    private val fileUtils = FileUtils
    var webView: WebView? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var webViewUrl: String = ""
    private var clearBrowserCache = false
    private val permissionUtil = PermissionUtil
    private lateinit var imageUri: Uri

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.webview.canGoBack()) {
                binding.webview.goBack()
            } else {
                //routeToHomeScreen()
                finish()
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityAvatarWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)
        try {
            if (intent.hasExtra(URL_KEY)) {
                webViewUrl = intent.getStringExtra(URL_KEY) ?: "https://demo.readyplayer.me/avatar"
            }
            if (intent.hasExtra(CLEAR_BROWSER_CACHE)) {
                clearBrowserCache = intent.getBooleanExtra(CLEAR_BROWSER_CACHE,false)
            }
            Utilities.printLogError("UrlToLoad--->$webViewUrl")
            Utilities.printLogError("ClearBrowserCache--->$clearBrowserCache")
            webView = binding.webview
            setUpToolbar()
            setUpWebView()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setUpWebView() {

        webView!!.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                executeJavascript()
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
                webView!!.visibility = View.VISIBLE
            }
        }

        webView!!.webChromeClient = object: WebChromeClient() { override fun onShowFileChooser(
            webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {

            this@AvatarWebViewActivity.filePathCallback = filePathCallback

            fileChooserParams?.let {
                Utilities.printData("fileChooserParams",it)
                Utilities.printLogError("isCaptureEnabled--->${it.isCaptureEnabled}")
                Utilities.printData("acceptTypes",it.acceptTypes)

                if ( fileChooserParams.isCaptureEnabled ) {
                    proceedWithCameraPermission()
                } else {
                    val acceptTypes = fileChooserParams.acceptTypes ?: emptyArray()
                    if (acceptTypes.contains("image/*")) {
                        openDocumentContract.launch("image/*")
                    } else {
                        proceedWithCameraPermission()
                    }
                }
            }
            return true
        }

            override fun onPermissionRequest(request: PermissionRequest?) {
                Utilities.printLogError("PERMISSION : onPermissionRequest: ${request?.resources}")
                //request?.grant(arrayOf(Manifest.permission.CAMERA))
                request!!.grant(request.resources) // Grant permissions requested by the WebView
            }
        }

        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.mediaPlaybackRequiresUserGesture = false
        webView!!.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView!!.settings.databaseEnabled =true
        webView!!.settings.useWideViewPort = true
        webView!!.settings.loadWithOverviewMode = true
        webView!!.settings.allowFileAccess = true
        webView!!.settings.domStorageEnabled = true
        webView!!.settings.allowFileAccessFromFileURLs = true
        webView!!.settings.javaScriptCanOpenWindowsAutomatically = true

        if ( clearBrowserCache ) {
            webView!!.clearWebViewData()
        }
        webView!!.loadUrl(webViewUrl)
        webView!!.addJavascriptInterface( WebViewInterface(this@AvatarWebViewActivity) { webMessage ->
            handleWebMessage(webMessage)
        } ,"WebView" )
    }

    private fun proceedWithCameraPermission() {
        val permissionResult: Boolean = permissionUtil.checkCameraPermission(object :
            PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    openCamera()
                }
            }
        }, this)
        if (permissionResult) {
            openCamera()
        }
    }

    private val openDocumentContract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if( it == null ) {
            Utilities.toastMessageShort(this,"No Image Selected !!")
            filePathCallback?.onReceiveValue(null)
        } else {
            it.let {
                filePathCallback?.onReceiveValue(arrayOf(it))
            }
        }
    }

    private val openCameraResultContract = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if ( success ) {
            // Use the imageUri for further processing
            Utilities.printLogError("ON RESULT : Image saved to: $imageUri")
            filePathCallback?.onReceiveValue(arrayOf(imageUri))
        } else {
            Utilities.toastMessageShort(this, "No Image captured !!")
            filePathCallback?.onReceiveValue(null)
        }
    }

    // Function to start the camera intent
    private fun openCamera() {
        val folderName = Utilities.getAppFolderLocation(this)
        //val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_PROFPIC", ".png")
        //val file = File(folderName,fileName)
        val imageFile = File.createTempFile("fromCamera",".png", File(folderName)).apply {
            createNewFile()
            deleteOnExit()
        }
        imageUri = Uri.fromFile(imageFile)
        openCameraResultContract.launch(imageUri)
    }

    private fun executeJavascript() {
        webView!!.evaluateJavascript("""
                var hasSentPostMessage = false;
                function subscribe(event) {
                    const json = parse(event);
                    const source = json.source;
                    
                    if (source !== 'readyplayerme') {
                      return;
                    }
                    
                    if (json.eventName === 'v1.frame.ready' && !hasSentPostMessage) {
                        window.postMessage(
                            JSON.stringify({
                                target: 'readyplayerme',
                                type: 'subscribe',
                                eventName: 'v1.**'
                            }),
                            '*'
                        );
                        hasSentPostMessage = true;
                    }

                    WebView.receiveData(event.data)
                }

                function parse(event) {
                    try {
                        return JSON.parse(event.data);
                    } catch (error) {
                        return null;
                    }
                }

                window.removeEventListener('message', subscribe);
                window.addEventListener('message', subscribe);
            """.trimIndent(), null)
    }

    private fun handleWebMessage(webMessage: WebViewInterface.WebMessage) {
        Utilities.printData("RPM : handleWebMessage",webMessage)
        when (webMessage.eventName) {
            WebViewInterface.WebViewEvents.USER_SET -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null"
                }
                callback?.onOnUserSet(userId)
            }
            WebViewInterface.WebViewEvents.USER_UPDATED -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null webMessage.data"
                }
                callback?.onOnUserUpdated(userId)
            }
            WebViewInterface.WebViewEvents.USER_AUTHORIZED -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null webMessage.data"
                }
                if ( imageUri != null  ) {
                    Utilities.deleteFile(imageUri.toFile())
                }
                callback?.onOnUserAuthorized(userId)
            }
            WebViewInterface.WebViewEvents.ASSET_UNLOCK -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'id' cannot be null webMessage.data"
                }
                val assetId = requireNotNull(webMessage.data[ASSET_ID_KEY]) {
                    "RPM: 'assetId' cannot be null webMessage.data"
                }
                val assetRecord = WebViewInterface.AssetRecord(userId, assetId)
                callback?.onAssetUnlock(assetRecord)
            }
            WebViewInterface.WebViewEvents.AVATAR_EXPORT -> {
                val avatarUrl = requireNotNull(webMessage.data["url"]) {
                    //"RPM: 'url' cannot be null in webMessage.data"
                    finishActivityWithFailure("RPM: avatar 'url' property not found in event data")
                }
                callback?.onAvatarExported(avatarUrl)
                finishActivityWithResult()
            }
            WebViewInterface.WebViewEvents.USER_LOGOUT -> {
                callback?.onUserLogout()
            }
        }
    }

    private fun finishActivityWithResult() {
        val resultString = "Avatar Created Successfully"
        val data = Intent()
        data.putExtra("result_key", resultString)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun finishActivityWithFailure(errorMessage: String) {
        val data = Intent()
        data.putExtra("error_key", errorMessage)
        setResult(Activity.RESULT_CANCELED, data)
        finish()
    }

    private fun WebView.clearWebViewData() {
        clearHistory()
        clearFormData()
        clearCache(true)
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().removeSessionCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = "Create Avatar"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
        binding.toolBarView.imgHelp.setImageResource(R.drawable.ic_close)
        binding.toolBarView.imgHelp.visibility = View.VISIBLE
        binding.toolBarView.imgHelp.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        binding.webview.destroy()
        super.onDestroy()
    }

    companion object {
        private const val ID_KEY = "id"
        private const val ASSET_ID_KEY = "assetId"
        const val CLEAR_BROWSER_CACHE = "clear_browser_cache"
        const val URL_KEY = "url_key"
        var callback: WebViewCallback? = null

        fun setWebViewCallback(callback: WebViewCallback) {
            this.callback = callback
        }
    }

    interface WebViewCallback {
        fun onAvatarExported(avatarUrl: String)
        fun onOnUserSet(userId: String)
        fun onOnUserUpdated(userId: String)
        fun onOnUserAuthorized(userId: String)
        fun onAssetUnlock(assetRecord: WebViewInterface.AssetRecord)
        fun onUserLogout()
    }

}