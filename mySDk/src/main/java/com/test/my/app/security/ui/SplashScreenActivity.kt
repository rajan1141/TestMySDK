package com.test.my.app.security.ui

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Configuration.EntityID
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivitySplashScreenBinding
import com.test.my.app.home.ui.HomeMainActivity
import com.test.my.app.home.ui.aktivo.AktivoPermissionsActivity
import com.test.my.app.security.ui_dialog.DialogUpdateAppSecurity
import com.test.my.app.security.util.RootUtil.isCheckMethodDME
import com.test.my.app.security.util.RootUtil.isCheckMethodOneDR
import com.test.my.app.security.util.SecurityChecks.checkMethodPro
import com.test.my.app.security.viewmodel.StartupViewModel
import com.google.firebase.FirebaseApp
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener,
    DialogUpdateAppSecurity.OnSkipUpdateListener {

    companion object {
        // Used to load the 'native-lib' library on application startup.
        /*init {
            System.loadLibrary("native-lib")
        }*/
    }

    private external fun init()
    private external fun checkForSuspiciousFiles(): Boolean
    private external fun isFridaDetected(): Boolean
    private external fun checkProcMaps(): Boolean
    private external fun checkProcTasks(): Boolean
    private external fun checkProcMounts(): Boolean
    private external fun checkProcExe(): Boolean


    private lateinit var binding: ActivitySplashScreenBinding

    private val viewModel: StartupViewModel by lazy {
        ViewModelProvider(this)[StartupViewModel::class.java]
    }

    lateinit var rootBeer: RootBeer
    var isRooted = false
    var from = ""

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P) object :
            BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                proceedWithAnimation()
            }


            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                finish()
            }
        }

    override fun getViewModel(): BaseViewModel = viewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(Constants.FROM)) {
            from = intent.getStringExtra(Constants.FROM)!!
        }
        Utilities.printLogError("SplashScreenActivity:from--->$from")

        val logoDesc =
            "<a>" + "<a><B><font color='#81A684'>${resources.getString(R.string.SPLASH_DESC1)}</font></B><br/><B><font color='#E8988D'>${
                resources.getString(R.string.SPLASH_DESC2)
            } - 2023</font></B>" + "</a>"
        binding.txtLogoDesc.text = Html.fromHtml(logoDesc)
        rootBeer = RootBeer(this)
        /*

        if (Constants.environment.equals("PROD")) {
            if (rootBeer.isRooted || rootBeer.isRootedWithBusyBoxCheck || this.isDeviceRooted) {
                //we found indication of root
                isRooted = true
                //showWebDialog(resources.getString(R.string.WARNING),resources.getString(R.string.WARNING_DESC))
                Utilities.toastMessageLong(this,resources.getString(R.string.WARNING_DESC))
                //finishAffinity()
                finishAndRemoveTask()
            }
            else if (this.isDeveloperModeEnabled()) {
                isRooted = true
                //showWebDialog(this.getString(R.string.KINDLY_DISABLE_THE_DEVELOPER_MODE_ON_YOUR_DEVICE),this.getString(R.string.FOR_SECURITY_REASONS_YOU_CANNOT_USE_OUR_APPLICATION_IF_THE_DEVELOPER_MODE_IS_ENABLED_ON_YOUR_DEVICE))
                Utilities.toastMessageLong(this,resources.getString(R.string.FOR_SECURITY_REASONS_YOU_CANNOT_USE_OUR_APPLICATION_IF_THE_DEVELOPER_MODE_IS_ENABLED_ON_YOUR_DEVICE))
                //finishAffinity()
                finishAndRemoveTask()
            }
            else {
                initFirebase()
            }
        } else {
            initFirebase()
        }*/

        if (rootBeer.isRooted || rootBeer.isRootedWithBusyBoxCheck) {
            isRooted = true
            Utilities.toastMessageLong(this, resources.getString(R.string.WARNING_DESC))
            finishAffinity()
        } else {
            checkSplashMethod()
            //initFirebase()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
     fun initFirebase() {
        isRooted = false
        FirebaseApp.initializeApp(this)
        setEntityId()
        if (viewModel.getLoginStatus()) {
            //if (viewModel.getBaseurlChangedStatus() || !viewModel.isFirstTimeUser() ) {
            if (viewModel.getBaseurlChangedStatus()) {
                if (Utilities.checkBiometricSupport(this)) {
                    if (viewModel.isBiometricAuthentication()) {
                        when (from) {
                            Constants.LOGIN, Constants.LOGIN_WITH_OTP -> {
                                if (viewModel.getEmployeeId().isNotEmpty() && viewModel.getOrgName() == Constants.SUD_ORG_NAME) {
                                    Utilities.setEmployeeType(Constants.SUD_LIFE)
                                    //Utilities.logCleverTapEmployeeEventLogin(this, Constants.SUD_LIFE)
                                    proceedWithAnimation()
                                } else {
                                    //showIsEmployeeDialog()
                                    Utilities.setEmployeeType(Constants.CUSTOMER)
                                    //Utilities.logCleverTapEmployeeEventLogin(this, employer.employerCode)
                                    proceedWithAnimation()
                                }
                            }

                            Constants.SIGN_UP_NEW -> {
                                proceedWithAnimation()
                            }

                            else -> showBiometricLock()
                        }
                    } else {
                        when (from) {
                            Constants.LOGIN, Constants.LOGIN_WITH_OTP -> {
                                if (viewModel.getEmployeeId().isNotEmpty() && viewModel.getOrgName() == Constants.SUD_ORG_NAME) {
                                    Utilities.setEmployeeType(Constants.SUD_LIFE)
                                    //Utilities.logCleverTapEmployeeEventLogin(this, Constants.SUD_LIFE)
                                    proceedWithAnimation()
                                } else {
                                    //showIsEmployeeDialog()
                                    Utilities.setEmployeeType(Constants.CUSTOMER)
                                    //Utilities.logCleverTapEmployeeEventLogin(this, employer.employerCode)
                                    proceedWithAnimation()
                                }
                            }

                            else -> proceedWithAnimation()
                        }
                    }
                } else {
                    when (from) {
                        Constants.LOGIN, Constants.LOGIN_WITH_OTP -> {
                            if (viewModel.getEmployeeId().isNotEmpty() && viewModel.getOrgName() == Constants.SUD_ORG_NAME) {
                                Utilities.setEmployeeType(Constants.SUD_LIFE)
                                //Utilities.logCleverTapEmployeeEventLogin(this, Constants.SUD_LIFE)
                                proceedWithAnimation()
                            } else {
                                //showIsEmployeeDialog()
                                Utilities.setEmployeeType(Constants.CUSTOMER)
                                //Utilities.logCleverTapEmployeeEventLogin(this, employer.employerCode)
                                proceedWithAnimation()
                            }
                        }

                        else -> proceedWithAnimation()
                    }
                }
            } else {
                viewModel.setBaseurlChangedStatus(true)
                viewModel.logoutFromDB()
                if (Utilities.logout(this, this)) {
                    //openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                   /* val intent = Intent(this,SecurityActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)*/
                    finish()
                }
            }
        } else {
            proceedWithAnimation()
//            proceedInApp()
        }
        registerObserver()
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun registerObserver() {
        viewModel.darwinBoxData.observe(this) { }
        viewModel.isLogin.observe(this) {}
        viewModel.isRegister.observe(this) {}
    }

    private fun proceedWithAnimation() {
        Handler(Looper.getMainLooper()).postDelayed({
            proceedInApp()
        }, (Constants.SPLASH_ANIM_DELAY_IN_MS).toLong())
    }

    private fun proceedInApp() {
        Utilities.preferenceUtils.storeBooleanPreference(Constants.BANNER_AD, false)
        try {
            setEntityId()
            if (intent.hasExtra(Constants.SCREEN)) {
                val screen = intent.getStringExtra(Constants.SCREEN)
                Utilities.printLogError("Screen(SplashActivity)--->$screen")
                val launchIntent = Intent()
                launchIntent.putExtra(Constants.SCREEN, screen)
                launchIntent.putExtra(Constants.NOTIFICATION_ACTION, intent.getStringExtra(Constants.NOTIFICATION_ACTION))
                launchIntent.putExtra(Constants.NOTIFICATION_TITLE, intent.getStringExtra(Constants.NOTIFICATION_TITLE))
                launchIntent.putExtra(Constants.NOTIFICATION_MESSAGE, intent.getStringExtra(Constants.NOTIFICATION_MESSAGE))
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                when (screen) {

                    "HRA", "FAMILY_HRA", "HRA_THREE" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HRA_HOME)
                    }

                    "STEPS", "STEPS_DAILY_TARGET", "STEPS_WEEKLY_SYNOPSIS" -> {
                        if (viewModel.isSelfUser()) {
                            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FITNESS_HOME)
                        } else {
                            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        }
                    }

                    "SMART_PHONE", "HEART_AGE_CALC", "DIABETES_CALCULATOR", "DASS_21", "HTN_CALC" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.TOOLS_CALCULATORS_HOME)
                    }

                    "VITALS" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.TRACK_PARAMETER_HOME)
                    }

                    "BLOG" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.BLOGS_HOME)
                    }

                    "FAMILY_MEMBER_ADD" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE)
                    }

                    "HEALTHTIPS" -> {
                        launchIntent.putExtra(Constants.SCREEN,"HEALTHTIPS")
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    }

                    "WATER_REMINDER", "WATER_REMINDER_21_POSITIVE", "WATER_REMINDER_21_NEGATIVE" -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.WATER_TRACKER_HOME)
                    }

                    Constants.SCREEN_FEATURE_CAMPAIGN -> {
                        if (intent.hasExtra(Constants.DEEP_LINK)) {
                            val deepLink = intent.getStringExtra(Constants.DEEP_LINK)
                            val deepLinkUri = Uri.parse(deepLink)
                            var deepLinkValue = ""
                            var deepLinkSub1 = ""
                            var deepLinkSub2 = ""
                            deepLinkValue = deepLinkUri.getQueryParameter(Constants.DEEP_LINK_VALUE)!!
                            deepLinkSub1 = deepLinkUri.getQueryParameter(Constants.DEEP_LINK_SUB1)!!
                            deepLinkSub2 = deepLinkUri.getQueryParameter(Constants.DEEP_LINK_SUB2)!!
                            Utilities.printLogError("deepLinkValue : $deepLinkValue")
                            Utilities.printLogError("deep_link_sub1 : $deepLinkSub1")
                            Utilities.printLogError("deep_link_sub2 : $deepLinkSub2")
                            if ( !Utilities.isNullOrEmpty(deepLinkValue) ) {
                                val campaignData = HashMap<String, Any>()
                                campaignData[CleverTapConstants.CAMPAIGN_NAME] = deepLinkValue
                                if( !Utilities.isNullOrEmpty(deepLinkSub1) ) {
                                    campaignData[CleverTapConstants.ADDITIONAL_PARAMETER_1] = deepLinkSub1
                                }
                                if( !Utilities.isNullOrEmpty(deepLinkSub2) ) {
                                    campaignData[CleverTapConstants.ADDITIONAL_PARAMETER_2] = deepLinkSub2
                                }
                                campaignData[CleverTapConstants.FROM_NOTIFICATION] = CleverTapConstants.YES
                                CleverTapHelper.pushEventWithProperties(applicationContext,CleverTapConstants.AF_CAMPAIGN,campaignData,false)
                            }
                            if ( !Utilities.isNullOrEmpty(deepLinkSub1)
                                && !Utilities.isNullOrEmpty(deepLinkSub2)
                                && deepLinkSub1 == Constants.DEEP_LINK_APP_FEATURE_CAMPAIGN ) {
                                Utilities.setCampaignFeatureDetails(deepLinkSub2)
                            }
                        }
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    }

                    Constants.SCREEN_EXTERNAL_URL_CAMPAIGN -> {
                        Utilities.redirectToChrome(intent.getStringExtra(Constants.DEEP_LINK)!!, this)
                        finishAndRemoveTask()
                        return
                    }

                    Constants.SCREEN_INTERNAL_URL_CAMPAIGN -> {
                        launchIntent.putExtra(Constants.SCREEN, intent.getStringExtra(Constants.SCREEN))
                        launchIntent.putExtra(Constants.WEB_URL, intent.getStringExtra(Constants.DEEP_LINK))
                        launchIntent.putExtra(Constants.FROM,Constants.NOTIFICATION)
                        launchIntent.putExtra(Constants.NOTIFICATION_TITLE, intent.getStringExtra(Constants.NOTIFICATION_TITLE))
                        launchIntent.putExtra(Constants.NOTIFICATION_MESSAGE, intent.getStringExtra(Constants.NOTIFICATION_MESSAGE))
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FEED_UPDATE_SCREEN)
                    }

                    else -> {
                        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    }
                }
                startActivity(launchIntent)
                cancelNotification()
            } else if (intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM).equals(Constants.NOTIFICATION_ACTION, ignoreCase = true)) {
                val launchIntent = Intent()
                launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                launchIntent.putExtra(Constants.DATE, intent.getStringExtra(Constants.DATE))
                launchIntent.putExtra(Constants.NOTIFICATION_ACTION, Constants.MEDICATION)
                launchIntent.putExtra(Constants.PERSON_ID, intent.getStringExtra(Constants.PERSON_ID))
                launchIntent.putExtra(Constants.MEDICINE_NAME, intent.getStringExtra(Constants.MEDICINE_NAME))
                launchIntent.putExtra(Constants.DOSAGE, intent.getStringExtra(Constants.DOSAGE))
                launchIntent.putExtra(Constants.INSTRUCTION, intent.getStringExtra(Constants.INSTRUCTION))
                launchIntent.putExtra(Constants.SCHEDULE_TIME, intent.getStringExtra(Constants.SCHEDULE_TIME))
                launchIntent.putExtra(Constants.MEDICATION_ID, intent.getStringExtra(Constants.MEDICATION_ID))
                launchIntent.putExtra(Constants.MEDICINE_IN_TAKE_ID, 0)
                launchIntent.putExtra(Constants.SERVER_SCHEDULE_ID, intent.getStringExtra(Constants.SERVER_SCHEDULE_ID))
                launchIntent.putExtra(Constants.DRUG_TYPE_CODE, intent.getStringExtra(Constants.DRUG_TYPE_CODE))
                launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
                startActivity(launchIntent)
                cancelNotification()
            }
            else if (intent.hasExtra(Constants.NOTIFICATION_TYPE)) {
                val aktivoIntent = Intent(this,AktivoPermissionsActivity::class.java)
                aktivoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                aktivoIntent.putExtra(Constants.FROM,Constants.NOTIFICATION)

                when (intent.getStringExtra(Constants.NOTIFICATION_TYPE)) {
                    "home" -> {
                        aktivoIntent.putExtra(Constants.CODE,Constants.AKTIVO_DASHBOARD_CODE)
                    }

                    "4pa","homeSedentary","homeExercise","homeSleep",
                    "statsAktivoScore","statsSteps","statsSleep","statsExercise","statsSb","statsLipa" -> {
                        aktivoIntent.putExtra(Constants.CODE,Constants.AKTIVO_SCORE_CODE)
                    }

                    "challenge", "challengeDetail" -> {
                        var challengeId = ""
                        if (intent.hasExtra(Constants.CHALLENGE_ID)) {
                            challengeId = intent.getStringExtra(Constants.CHALLENGE_ID)!!
                        }
                        aktivoIntent.putExtra(Constants.CODE,Constants.AKTIVO_CHALLENGES_CODE)
                        aktivoIntent.putExtra(Constants.CHALLENGE_ID,challengeId)
                    }

                    else -> {
                        aktivoIntent.putExtra(Constants.CODE,Constants.AKTIVO_DASHBOARD_CODE)
                    }
                }
                startActivity(aktivoIntent)
                cancelNotification()
            }
/*            else if (intent.hasExtra(Constants.NOTIFICATION_TYPE)) {
                when (intent.getStringExtra(Constants.NOTIFICATION_TYPE)) {
                    "home" -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_DASHBOARD_CODE)
                            putString(Constants.FROM, Constants.NOTIFICATION)
                        }
                    }

                    "4pa","homeSedentary","homeExercise","homeSleep",
                    "statsAktivoScore","statsSteps","statsSleep","statsExercise","statsSb","statsLipa" -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_SCORE_CODE)
                            putString(Constants.FROM, Constants.NOTIFICATION)
                        }
                    }

                    "challenge", "challengeDetail" -> {
                        var challengeId = ""
                        if (intent.hasExtra(Constants.CHALLENGE_ID)) {
                            challengeId = intent.getStringExtra(Constants.CHALLENGE_ID)!!
                        }
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_CHALLENGES_CODE)
                            putString(Constants.FROM, Constants.NOTIFICATION)
                            putString(Constants.CHALLENGE_ID, challengeId)
                        }
                    }

                    else -> {
                        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
                            putString(Constants.CODE, Constants.AKTIVO_DASHBOARD_CODE)
                            putString(Constants.FROM, Constants.NOTIFICATION)
                        }
                    }
                }
                cancelNotification()
            }*/
            else {
                val isDarwinBoxDetailsAvailable = Utilities.getBooleanPreference(PreferenceConstants.IS_DARWINBOX_DETAILS_AVAILABLE)
                val darwinBoxUrl = Utilities.getUserPreference(Constants.DARWINBOX_URL)
                if (viewModel.isFirstTimeUser()) {
                    Utilities.printLogError("First_Time_User")
                    if (isDarwinBoxDetailsAvailable) {
                        viewModel.callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl, this)
                        Utilities.clearDarwinBoxDetails()
                    } else {
                        startActivity(Intent(this, AppIntroductionActivity::class.java))
                        finish()
                    }
                } else {
                    if (viewModel.getLoginStatus()) {
                        Utilities.printLogError("User_Logged_In")
                        if (isDarwinBoxDetailsAvailable) {
                            viewModel.logoutFromDB()
                            Utilities.logout(this, this)
                            Utilities.printLogError("Logout for Darwinbox")
                            viewModel.callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl, this)
                            Utilities.clearDarwinBoxDetails()
                        } else {
                            //openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true, animate = false)
                            val intent = Intent(this, HomeMainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Utilities.printLogError("User_not_Logged_In")
                        if (isDarwinBoxDetailsAvailable) {
                            viewModel.callGetLoginInfoWithDarwinBoxApi(darwinBoxUrl, this)
                            Utilities.clearDarwinBoxDetails()
                        } else {
                            //openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                           /* val intent = Intent(this,SecurityActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)*/
                            finish()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSkipUpdate() {
        proceedInApp()
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight && isRooted) {
            finishAffinity()
        }
    }

    private fun cancelNotification() {
        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID,-1)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
        }
    }

    fun checkForSuspiciousActivities(): Boolean {
        return checkForSuspiciousFiles() || isFridaDetected() || checkProcMaps() || checkProcTasks() || checkProcMounts() ||  checkProcExe()
    }

    private fun setEntityId() {
        try {
            EntityID = if (Utilities.getLoginStatus()) {
                viewModel.getMainUserPersonID().ifEmpty { "0" }
            } else {
                "0"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBiometricLock() {
        try {
            val biometricPrompt = BiometricPrompt.Builder(this@SplashScreenActivity)
                //.setDeviceCredentialAllowed(true) // not supported on Android 10 & below devices
                .setTitle(resources.getString(R.string.BIOMETRIC_AUTHENTICATION))
                .setSubtitle(resources.getString(R.string.BIOMETRIC_AUTHENTICATION_DESC))
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                biometricPrompt.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            }
            biometricPrompt.build().authenticate(Utilities.getCancellationSignal(), mainExecutor, authenticationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkSplashMethod() {
/*        if (!BuildConfig.MY_APP_HASH.equals(computeSigningCertificateHash(this), false)) {
            Utilities.toastMessageLong(this, getString(R.string.app_hash_does_not_match))
            finishAffinity()
        } else */
            if (Constants.environment.equals("PROD",false)) {
            if (this.isCheckMethodOneDR) {
                isRooted = true
                Utilities.toastMessageLong(this, resources.getString(R.string.WARNING_DESC))
                finishAffinity()
            }
/*            else if (this.isCheckMethodVConnected) {
                isRooted = true
                Utilities.toastMessageLong(
                    this, getString(R.string.this_device_appears_to_be_connected_to_a_vpn)
                )
                finishAffinity()
            } */
            else if (checkMethodPro()) {
                isRooted = true
                Utilities.toastMessageLong(
                    this, getString(R.string.this_device_appears_to_be_connected_to_a_proxy_server)
                )
                finishAffinity()
            }
/*            else if (isCheckMethodAppT() || checkForSuspiciousActivities()) {
                isRooted = true

                Utilities.toastMessageLong(this, getString(R.string.this_device_appears_to_be_tampered))
                finishAffinity()
            } */
            else if (this.isCheckMethodDME()) {
                isRooted = true
                Utilities.toastMessageLong(
                    this,
                    resources.getString(R.string.FOR_SECURITY_REASONS_YOU_CANNOT_USE_OUR_APPLICATION_IF_THE_DEVELOPER_MODE_IS_ENABLED_ON_YOUR_DEVICE)
                )
                finishAffinity()
            } else {
                initFirebase()
            }
        }
        else {
            initFirebase()
        }
    }

/*    private fun registerObserver() {

        viewModel.checkAppUpdate.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    Utilities.printData("UpdateData", it.data, true)
                    if (it.data.result.appVersion.isNotEmpty()) {
                        val versionDetails = it.data.result.appVersion[0]
                        val currentVersion = versionDetails.currentVersion!!.toDouble().toInt()
                        val forceUpdate = versionDetails.forceUpdate

                        val existingVersion = Utilities.getAppVersion(this)
                        Utilities.printLogError("CurrentVersion---->$currentVersion")
                        Utilities.printLogError("ExistingVersion--->$existingVersion")
                        if (existingVersion < currentVersion) {
                            if (forceUpdate) {
                                viewModel.logoutFromDB()
                                if (Utilities.logout(this, this)) {
                                    openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true) {
                                        putString(Constants.FROM, Constants.LOGOUT)
                                        putBoolean(Constants.FORCEUPDATE, versionDetails.forceUpdate)
                                        putString(Constants.DESCRIPTION, versionDetails.description!!)
                                    }
                                }
                            } else {
                                dialogUpdateApp = DialogUpdateAppSecurity(this, versionDetails, this)
                                dialogUpdateApp!!.show()
                            }
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                proceedInApp()
                            }, (Constants.SPLASH_ANIM_DELAY_IN_MS).toLong())
                        }
                    }
                }
            }


            if (it.status == Resource.Status.ERROR) {
                //Utilities.printLog("ERROR--->${it.errorMessage} :: ${it.errorNumber}")
                if (it.errorNumber.equals("0", true)) {
                    showDialog(
                        listener = this,
                        title = resources.getString(R.string.AWW_SNAP),
                        message = resources.getString(R.string.UNEXPECTED_ERROR),
                        showLeftBtn = false
                    )
                }
            }

        }
    }*/

}