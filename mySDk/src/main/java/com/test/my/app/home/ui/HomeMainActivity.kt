package com.test.my.app.home.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aktivolabs.aktivocore.managers.AktivoManager
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseAdapter
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.extension.replaceFragment
import com.test.my.app.common.extension.replaceFragmentWithoutStack
import com.test.my.app.common.extension.visibilityView
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.PermissionUtil
import com.test.my.app.common.utils.UserSingleton
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.showDialog
import com.test.my.app.databinding.ActivityHomeMainBinding
import com.test.my.app.home.adapter.AdapterHomeDrawer
import com.test.my.app.home.common.HRAObservationModel
import com.test.my.app.home.common.PolicyDataSingleton
import com.test.my.app.home.di.ScoreListener
import com.test.my.app.home.ui.sudLifePolicy.FragmentSudPolicyAuthentication
import com.test.my.app.home.ui.sudLifePolicy.SudLifePolicyDashboardFragment
import com.test.my.app.home.viewmodel.BackgroundCallViewModel
import com.test.my.app.home.viewmodel.DashboardViewModel
import com.test.my.app.model.DrawerData
import com.test.my.app.common.constants.Configuration.EntityID
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.repository.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeMainActivity : BaseActivity(),DefaultNotificationDialog.OnDialogValueListener,BaseAdapter.OnItemClickListener {
    private val fileUtils = FileUtils
    private var toggle: ActionBarDrawerToggle? = null
    private var adapterHomeDrawer: AdapterHomeDrawer? = null
    private val drawerList: ArrayList<DrawerData> = ArrayList()
    val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val backGroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }
    lateinit var binding: ActivityHomeMainBinding
    private var selectedTab = 0
    private val appColorHelper = AppColorHelper.instance!!
    private val permissionUtil = PermissionUtil
    private lateinit var scoreListener: ScoreListener
    private lateinit var swichProfileListener: OnSwitchProfileClickListener
    private var googleAccountListener: OnGoogleAccountSelectListener? = null
    private var aktivoListener: OnAktivoListener? = null
    private var onHelpClickListener: OnHelpClickListener? = null
    private var exit: Boolean = false

    override fun getViewModel(): BaseViewModel = backGroundCallViewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val fragment = supportFragmentManager.findFragmentById(R.id.main_container)
            when {

                fragment is HomeScreenFragment|| fragment is SudLifePolicyDashboardFragment || fragment is FragmentSudPolicyAuthentication  || fragment is FragmentTrackHealth -> {
                    backAction()
                }
                fragment is FragmentProfile->{
                    gotoHome()
                }
                supportFragmentManager.backStackEntryCount > 0 -> {
                    supportFragmentManager.popBackStack()
                    Utilities.printLogError("selectedTab--->$selectedTab")
                }

                else -> {
                    when (selectedTab) {
                        0 -> {
                            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                binding.drawerLayout.closeDrawer(GravityCompat.START)
                            }
                        }

                        else -> {
                            gotoHome()
                        }
                    }
                    refreshUI()
                }
            }


        }

    }

    private fun refreshUI() {

        lifecycleScope.launch(Dispatchers.Main) {
            delay(400)
            runOnUiThread {
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_navigation_menu)
                binding.bottomNavigation.selectedItemId = R.id.page_1
                initFilterList()
            }
        }
    }

    fun backAction() {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.EXIT_TITLE)
        dialogData.message = resources.getString(R.string.EXIT_CONFIRM)
        dialogData.btnLeftName = resources.getString(R.string.NO)
        dialogData.btnRightName = resources.getString(R.string.YES)
        val defaultNotificationDialog = DefaultNotificationDialog(this,
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        Utilities.clearCampaignFeatureDetails()
                        //finishAffinity()
                        finishAndRemoveTask()
                    }
                } },dialogData)
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

/*    fun backAction1(): Boolean {
        when {
            exit -> {
                Utilities.clearCampaignFeatureDetails()
                finishAffinity()
            }

            else -> {
                showToast(resources.getString(R.string.EXIT_MES))
                exit = true
                Handler().postDelayed({ exit = false }, (2 * 1000).toLong())
            }
        }
        return exit

    }*/

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityHomeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedTab = 0
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        EntityID = viewModel.getMainUserPersonID().ifEmpty { "0" }
        checkIsEmployee()
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        setToolbarInfo(0, showAppLogo = true, title = "", showBg = true)
        initialise()
        initObserver()
        callDashboardAPIs()

        viewModel.callGetProfileImageApiMain(viewModel.profileImageID)
        viewModel.callCheckAppUpdateApi(this,this)

        toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.NAVIGATION_DRAWER_OPEN,
            R.string.NAVIGATION_DRAWER_CLOSE
        ) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                // Do whatever you want here
                viewModel.setDrawerUserDetails(
                    binding.imgUser,
                    binding.txtUsername,
                    binding.txtUserEmail,
                    binding.imgSudBanner,
                    binding.imgBoiBanner,
                    binding.imgUbiBanner
                )
                Utilities.printLogError("Drawer Opened")
            }

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                // Do whatever you want here


                Utilities.printLogError("Drawer Closed")
            }
        }
        toggle!!.drawerArrowDrawable.color = appColorHelper.primaryColor()
        binding.drawerLayout.addDrawerListener(toggle!!)
        toggle!!.syncState()

        configureDrawerRecyclerView()

        Utilities.printLog("HOME onCreate")

        backGroundCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()


        checkDeepLink()

        viewModel.checkAppUpdate.observe(this) {

        }
        viewModel.profileImage.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val document = it.data!!.healthRelatedDocument
                val fileName = document.fileName
                val fileBytes = document.fileBytes
                try {
                    val path = Utilities.getAppFolderLocation(this)
                    val completeFilePath = "$path/$fileName"
                    val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)

                    try {
                        UserSingleton.getInstance()!!.profPicBitmap = decodedImage
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }
        }

    }


    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)

        val fragment = supportFragmentManager.findFragmentById(R.id.main_container)
        when (fragment) {
            is FragmentTrackHealth, is HomeScreenFragment, is FragmentProfile, is SudLifePolicyDashboardFragment, is FragmentSudPolicyAuthentication -> {
                if (supportActionBar != null) {
                    supportActionBar!!.setHomeButtonEnabled(true)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.setHomeAsUpIndicator(R.drawable.img_hamberger)//your icon here
                }
            }
            else -> {
                if (supportActionBar != null) {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.setDisplayShowTitleEnabled(false)
                    supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
                }
            }
        }


    }


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    PolicyDataSingleton.getInstance()!!.clearData()
                    gotoHome()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.page_2 -> {
                    PolicyDataSingleton.getInstance()!!.clearData()
                    replaceFragment(FragmentTrackHealth(), frameId = R.id.main_container)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.page_5 -> {
                    PolicyDataSingleton.getInstance()!!.clearData()
                    CleverTapHelper.pushEvent(this, CleverTapConstants.SUD_POLICY)
                    if (viewModel.getOtpAuthenticatedStatus()) {
                        replaceFragment(SudLifePolicyDashboardFragment(), frameId = R.id.main_container)

                    } else {
                        replaceFragment(FragmentSudPolicyAuthentication(), frameId = R.id.main_container)
                    }
                    return@OnNavigationItemSelectedListener true
                }

                R.id.page_4 -> {
                    navigateToProfile()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun navigateToAmahaWebview(moduleCode: String, title: String) {
        openAnotherActivity(destination = NavigationConstants.AMAHA_WEB_VIEW_SCREEN) {
            putString(Constants.MODULE_CODE, moduleCode)
            putString(Constants.TITLE, title)
        }
    }

    private fun navigateToProfile() {
        PolicyDataSingleton.getInstance()!!.clearData()
        CleverTapHelper.pushEvent(this, CleverTapConstants.MY_PROFILE_SCREEN)
        replaceFragment(FragmentProfile(), frameId = R.id.main_container)
    }


    fun isDrawerOpen(): Boolean {
        return binding.drawerLayout.isOpen
    }

    fun closeDrawer() {
        binding.drawerLayout.close()
    }

    fun openDrawer() {
        binding.drawerLayout.open()
    }

    private fun initialise() {
        initFilterList()
        binding.bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.bottomNavigation.setOnNavigationItemReselectedListener { }
        gotoHome()
        binding.bottomNavigation.background = null

        binding.imgUser.setOnClickListener {
            val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
            if (viewModel.relationshipCode == Constants.SELF_RELATIONSHIP_CODE && profPicBitmap != null) {
                Utilities.showFullImageWithBitmap(profPicBitmap, this, true)
            }
        }

        binding.appIconContainer.setOnClickListener {
            binding.drawerLayout.close()
            binding.bottomNavigation.selectedItemId = R.id.page_4
        }

        binding.imgBack.setOnClickListener {
            //drawerLayout.close()
            binding.bottomNavigation.selectedItemId = R.id.page_1
        }

        binding.imgHelp.setOnClickListener {
            if (onHelpClickListener != null) {
                onHelpClickListener!!.onHelpClick()
            }
        }

        binding.imgLogoForbes.setOnClickListener {
            val abc = BitmapFactory.decodeResource(resources,R.drawable.img_forbes_square)
            Utilities.showFullImageWithBitmap(abc, this,true)
        }

        binding.imgForbes.setOnClickListener {
            val abc = BitmapFactory.decodeResource(resources,R.drawable.img_forbes_square)
            Utilities.showFullImageWithBitmap(abc, this,true)
        }
    }

    private fun gotoHome() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragmentWithoutStack(HomeScreenFragment(), frameId = R.id.main_container)
    }


    @SuppressLint("Recycle")
    internal fun initFilterList() {
        drawerList.clear()

        val names = resources.getStringArray(R.array.drawerList)
        val drawerIcon = resources.obtainTypedArray(R.array.drawerListIcon)


        for (i in names.indices) {

            drawerList.add(
                DrawerData(
                    name = names[i],
                    icon = drawerIcon.getResourceId(i, 0),
                    type = Constants.HOW_IT_WORKS_DRAWER + i
                )
            )
        }


        adapterHomeDrawer = AdapterHomeDrawer(drawerList)
        adapterHomeDrawer?.setOnItemClickListener(this)
        binding.drawerRV.adapter = adapterHomeDrawer
        binding.drawerRV.scheduleLayoutAnimation()


    }


    private fun initObserver() {
        viewModel.saveCloudMessagingId.observe(this) {}
        // Mayuresh: This observer is written for getting parameter data in dashboard screen
        backGroundCallViewModel.labRecordList.observe(this) {}
        backGroundCallViewModel.labParameterList.observe(this) {
            //scoreListener.onVitalDataUpdateListener(it)
        }

        backGroundCallViewModel.stepsHistoryList.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                //Utilities.printLogError("stepsHistoryList--->$data")
            }
        }
    }


    private fun navigateToMedicineDashboard() {
        Utilities.printLogError("Intent--->$intent")
        openAnotherActivity(destination = NavigationConstants.MEDICINE_TRACKER) {
            putString(Constants.FROM, Constants.NOTIFICATION_ACTION)
            putString(Constants.DATE, intent.getStringExtra(Constants.DATE))
        }
    }


    private fun callDashboardAPIs() {
        viewModel.getLoggedInPersonDetails()
    }

    @SuppressLint("SetTextI18n")
    private fun configureDrawerRecyclerView() {
        val versionName = Utilities.getVersionName(this)
        var env = ""
        if (!Utilities.isNullOrEmpty(versionName)) {
            val versionText =
                "${resources.getString(R.string.POWERED_BY_SUD_LIFE)} (v $versionName )"
            if (Constants.environment.equals("UAT", ignoreCase = true)) {
                env = " UAT"
            }
            binding.txtVersionName.text = versionText + env
        }
    }

    override fun onResume() {
        super.onResume()

        Utilities.printLogError("Inside onResume")
        var showProgress = false
        if (intent.hasExtra(Constants.FROM)
            && intent.getStringExtra(Constants.FROM).equals(Constants.NOTIFICATION_ACTION, ignoreCase = true)) {
            showProgress = true
            backGroundCallViewModel.isBackgroundApiCall = false
        }

        if (showProgress) {
            navigateToMedicineDashboard()
        } else {
            //backGroundCallViewModel.getAppVersionDetails(this)
            //backGroundCallViewModel.callCheckAppUpdateApi(this)
        }

        backGroundCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()
        backGroundCallViewModel.callBackgroundApiCall(showProgress)


        try {
            if(FirebaseApp.getApps(this).isNotEmpty()){
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Utilities.printLogError("HomeMainActivity : Fetching FCM registration token failed : $task.exception")
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result!!
                    Utilities.printLogError("\nToken=>$token")
                    viewModel.callSaveCloudMessagingIdApi(token, true)
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onStop() {
        super.onStop()
        backGroundCallViewModel.isBackgroundApiCall = false

    }


    /*@Deprecated("Deprecated in Java")
    override fun onBackPressed() {



    }*/


    //Required to set New Intent from Medicine Notification
    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.home_main, menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                when (supportFragmentManager.findFragmentById(R.id.main_container)) {
                    is FragmentTrackHealth, is HomeScreenFragment, is FragmentProfile, is SudLifePolicyDashboardFragment, is FragmentSudPolicyAuthentication -> {
                        if (toggle!!.onOptionsItemSelected(item)) {
                            return true
                        }
                    }

                    else -> {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun registerListener(dashboardFragment: ScoreListener) {
        scoreListener = dashboardFragment
    }

    fun setBottomNavigationView(navView: BottomNavigationView, cfm: FragmentManager) {

    }

    fun setNavigationId(page: Int = R.id.page_1) {
        binding.bottomNavigation.selectedItemId = page
    }

    fun registerSwitchProfileListener(listener: OnSwitchProfileClickListener) {
        swichProfileListener = listener
    }

    /*override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            CleverTapHelper.pushEvent(this, CleverTapConstants.LOGOUT)
            backGroundCallViewModel.logoutFromDB()
            if(Utilities.logout(this,this)) {
//                openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                finish()
            }
        }
    }

    fun setDataReceivedListener(listener: OnGoogleAccountSelectListener) {
        this.googleAccountListener = listener
    }

    fun setOnAktivoListener(listener: OnAktivoListener) {
        this.aktivoListener = listener
    }

    fun setOnHelpClickListener(listener: OnHelpClickListener) {
        this.onHelpClickListener = listener
    }

    @SuppressLint("ResourceType")
    fun setToolbarInfo(
        tabPosition: Int,
        showAppLogo: Boolean,
        showToolBar: Boolean = true,
        title: String = "",
        showBg: Boolean = false,
        showImgHelp: Boolean = false,
        showBottomNavigation: Boolean = true,
        isView3: Boolean = true,
        updateStatusBgColor: Int = R.color.white
    ) {
        selectedTab = tabPosition



        setToolbar()

        if (showAppLogo) {
            binding.imgLogoSmall.visibility = View.VISIBLE
        } else {
            binding.imgLogoSmall.visibility = View.GONE
        }
        if (showImgHelp) {
            binding.imgHelp.visibility = View.VISIBLE
        } else {
            binding.imgHelp.visibility = View.GONE
        }

        if (title.isNotEmpty()) {
            binding.txtTitle.text = title
        } else {
            binding.txtTitle.text = ""
        }

        if (showToolBar) {
            binding.toolbar.visibility = View.VISIBLE
        } else {
            binding.toolbar.visibility = View.GONE
        }

        if (showBottomNavigation) {
            binding.bottomNavigation.visibility = View.VISIBLE
        } else {
            binding.bottomNavigation.visibility = View.GONE
        }

        if (showBg) {
            checkIsEmployee()
        } else {
            updateStatusBarColor(updateStatusBgColor, false)
        }
        binding.view3.visibilityView(isView3)

    }

    @SuppressLint("ResourceType")
    fun checkIsEmployee() {
        when (Utilities.getEmployeeType()) {
            Constants.SUD_LIFE -> {
                updateStatusBarColor(R.mipmap.bg_dash, false)
                binding.imgLogoSud.visibility = View.VISIBLE
                binding.imgLogoBoi.visibility = View.GONE
                binding.imgLogoUbi.visibility = View.GONE
            }

/*            Constants.BOI -> {
                updateStatusBarColor(R.mipmap.bg_dash_boi, false)
                binding.imgLogoSud.visibility = View.GONE
                binding.imgLogoBoi.visibility = View.VISIBLE
                binding.imgLogoUbi.visibility = View.GONE
            }

            Constants.UBI -> {
                updateStatusBarColor(R.mipmap.bg_dash_ubi, false)
                binding.imgLogoSud.visibility = View.GONE
                binding.imgLogoBoi.visibility = View.GONE
                binding.imgLogoUbi.visibility = View.VISIBLE
            }*/

            else -> {
                updateStatusBarColor(R.mipmap.bg_dash, false)
                binding.imgLogoSud.visibility = View.GONE
                binding.imgLogoBoi.visibility = View.GONE
                binding.imgLogoUbi.visibility = View.GONE
            }
        }
    }

    interface OnGoogleAccountSelectListener {
        fun onGoogleAccountSelection(from: String)
    }

    interface OnAktivoListener {
        fun onAktivoSelection(from: String)
    }

    interface OnHelpClickListener {
        fun onHelpClick()
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utilities.printLogError("requestCode-> $requestCode")
        Utilities.printLogError("resultCode-> $resultCode")
        Utilities.printLogError("data-> $data")
        when (requestCode) {
            Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS -> {
                if (resultCode == RESULT_OK) {
                    Utilities.printLogError("All Google fit permissions granted from 1")
                    //Utilities.toastMessageShort(this,"All Google fit permissions granted from 1")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //checkPhysicalActivityPermission()
                        if (aktivoListener != null) {
                            aktivoListener!!.onAktivoSelection(Constants.PHYSICAL_ACTIVITY_PERMISSION)
                        }
                    } else {
                        //syncFitnessData()
                        if (aktivoListener != null) {
                            aktivoListener!!.onAktivoSelection(Constants.SYNC_FITNESS_DATA)
                        }
                    }
                } else {
                    Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_GOOGLE_FIT_PERMISSION))
                    if (aktivoListener != null) {
                        aktivoListener!!.onAktivoSelection(Constants.DENIED)
                    }
                }
            }

            /*            FitRequestCode.READ_DATA.value -> {
                            if (resultCode == RESULT_OK) {
                                Utilities.printLogError("onActivityResult READ_DATA")
                                if (googleAccountListener != null) {
                                    googleAccountListener!!.onGoogleAccountSelection(Constants.SUCCESS)
                                    backGroundCallViewModel.getStepsHistory()
                                }
                            }
                        }*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Utilities.printLogError("onRequestPermissionsResult from Home Main Activity")
        when (requestCode) {
            Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.printLogError("All Google fit permissions granted from 2")
                    //Utilities.toastMessageShort(this,"All Google fit permissions granted from 2")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //checkPhysicalActivityPermission()
                        if (aktivoListener != null) {
                            aktivoListener!!.onAktivoSelection(Constants.PHYSICAL_ACTIVITY_PERMISSION)
                        }
                    } else {
                        //syncFitnessData()
                        if (aktivoListener != null) {
                            aktivoListener!!.onAktivoSelection(Constants.SYNC_FITNESS_DATA)
                        }
                    }
                } else {
                    Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_GOOGLE_FIT_PERMISSION))
                    if (aktivoListener != null) {
                        aktivoListener!!.onAktivoSelection(Constants.DENIED)
                    }
                }
            }

            Constants.REQ_PHYSICAL_ACTIVITY_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.printLogError("Physical Activity permissions granted")
                    //syncFitnessData()
                    if (aktivoListener != null) {
                        aktivoListener!!.onAktivoSelection(Constants.SYNC_FITNESS_DATA)
                    }
                } else {
                    Utilities.toastMessageShort(
                        this, resources.getString(R.string.ERROR_PHYSICAL_ACTIVITY_PERMISSION)
                    )
                    if (aktivoListener != null) {
                        aktivoListener!!.onAktivoSelection(Constants.DENIED)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        permissionUtil.checkNotificationPermission(object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    Utilities.toastMessageShort(
                        this@HomeMainActivity,
                        resources.getString(R.string.MSG_NOTIFICATION_PERMISSION)
                    )
                }
            }
        }, this)
    }

    interface OnSwitchProfileClickListener {
        fun onClick()
    }

    private fun invalidateAktivoData() {
        try {
            val aktivoManager = AktivoManager.getInstance(this)
            aktivoManager!!.invalidateUser(this).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onComplete() {
                        //Utilities.toastMessageShort(this@HomeMainActivity, "User invalidated")
                        Utilities.printLogError("User invalidated")
                    }

                    override fun onError(e: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkDeepLink() {
        Utilities.printLogError("checkDeepLinkFromHomeActivity")
        if (Utilities.preferenceUtils.getBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE)) {
            Utilities.printLogError("DeepLinkResult Found in HomeActivity")
            val referralName =
                Utilities.preferenceUtils.getPreference(CleverTapConstants.REFERRAL_NAME)
            val referralPID =
                Utilities.preferenceUtils.getPreference(CleverTapConstants.REFERRAL_PID)
            val pid = viewModel.personId
            val data = HashMap<String, Any>()
            data[CleverTapConstants.REFERRAL_NAME] = referralName
            data[CleverTapConstants.REFERRAL_PID] = referralPID
            CleverTapHelper.pushEventWithProperties(
                this@HomeMainActivity, CleverTapConstants.ALREADY_LOGGED_IN_BY_REFERRAL, data
            )
            Utilities.preferenceUtils.storeBooleanPreference(
                PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE, false
            )
            //AddFeatureAccessLog
            val desc = "ReferralName:$referralName|ReferralPID:$referralPID|PersonID:$pid"
            viewModel.callAddFeatureAccessLogApi(
                Constants.ALREADY_LOGGED_IN_BY_REFERRAL, desc, Constants.REFERRAL, ""
            )
        }
    }

    override fun onItemClick(vararg itemData: Any) {
        if (itemData.isNotEmpty()) {
            val pos = itemData[0] as Int

            when (drawerList[pos].type) {

                /*                Constants.HOME_DRAWER -> {
                                    closeDrawer()
                                    gotoHome()
                                }

                                Constants.TRACK_DRAWER -> {
                                    closeDrawer()
                                    replaceFragment(FragmentTrackHealth(), frameId = R.id.main_container)
                                }

                                Constants.YOUR_POLICY_DRAWER -> {
                                    closeDrawer()
                                    CleverTapHelper.pushEvent(this, CleverTapConstants.SUD_POLICY)
                                    if (viewModel.getOtpAuthenticatedStatus()) {
                                        replaceFragment(
                                            SudLifePolicyDashboardFragment(),
                                            frameId = R.id.main_container
                                        )
                                    } else {
                                        replaceFragment(
                                            FragmentSudPolicyAuthentication(),
                                            frameId = R.id.main_container
                                        )
                                    }
                                }

                                Constants.PROFILE_DRAWER -> {
                                    closeDrawer()
                                    val permissionResult = permissionUtil.checkStoragePermission(object :
                                        PermissionUtil.AppPermissionListener {
                                        override fun isPermissionGranted(isGranted: Boolean) {
                                            Utilities.printLogError("$isGranted")
                                            if (isGranted) {
                                                navigateToProfile()
                                            }
                                        }
                                    }, this)
                                    if (permissionResult) {
                                        navigateToProfile()
                                    }
                                }*/

                Constants.REFER_A_FRIEND_DRAWER -> {
                    closeDrawer()
                    CleverTapHelper.pushEvent(this, CleverTapConstants.INVITE_APP)
                    viewModel.shareAppInvite()
                }

                Constants.HOW_IT_WORKS_DRAWER -> {
                    closeDrawer()
                    if (onHelpClickListener != null) {
                        onHelpClickListener!!.onHelpClick()
                    }
                }

                Constants.SETTINGS_DRAWER -> {
                    closeDrawer()
                    CleverTapHelper.pushEvent(this, CleverTapConstants.SETTINGS)
                    openAnotherActivity(destination = NavigationConstants.SETTINGS)
                }

                Constants.LANGUAGE_DRAWER -> {
                    closeDrawer()
                    openAnotherActivity(destination = NavigationConstants.LANGUAGE_SCREEN) {
                        putString(Constants.FROM, "")
                    }
                }

                Constants.PRIVACY_POLICY_DRAWER -> {
                    closeDrawer()
                    openAnotherActivity(destination = NavigationConstants.TERMS_CONDITION) {
                        putString(Constants.FROM, Constants.PRIVACY_POLICY)
                    }
                }

                Constants.TERMS_AND_CONDITIONS_DRAWER -> {
                    closeDrawer()
                    openAnotherActivity(destination = NavigationConstants.TERMS_CONDITION) {
                        putString(Constants.FROM, Constants.TERMS_CONDITIONS)
                    }
                }

                Constants.LOGOUT_DRAWER -> {
                    closeDrawer()
                    showDialog(
                        listener = this,
                        title = resources.getString(R.string.LOGOUT),
                        message = resources.getString(R.string.MSG_LOGOUT_CONFORMATION),
                        leftText = resources.getString(R.string.NO),
                        rightText = resources.getString(R.string.YES)
                    )
                }
            }

        }
    }

}

object HRAData {
    var data: HRAObservationModel = HRAObservationModel()
}