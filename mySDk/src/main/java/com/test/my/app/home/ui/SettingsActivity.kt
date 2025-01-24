package com.test.my.app.home.ui

import android.os.Bundle
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aktivolabs.aktivocore.managers.AktivoManager
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.NavigationConstants
import com.test.my.app.common.extension.openAnotherActivity
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.showDialog
import com.test.my.app.databinding.ActivitySettingsNewBinding
import com.test.my.app.home.adapter.OptionSettingsAdapter
import com.test.my.app.home.common.DataHandler
import com.test.my.app.home.viewmodel.BackgroundCallViewModel
import com.test.my.app.home.viewmodel.SettingsViewModel
import com.test.my.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : BaseActivity(), OptionSettingsAdapter.SettingsOptionListener,
    DefaultNotificationDialog.OnDialogValueListener {


    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    //private val dashboardViewModel: DashboardViewModel by
    private val backgroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }
    private lateinit var binding: ActivitySettingsNewBinding

    private var optionSettingsAdapter: OptionSettingsAdapter? = null
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_new)
        binding = ActivitySettingsNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        initialise()
    }

    private fun initialise() {
        Utilities.printLog("Language=> " + LocaleHelper.getLanguage(this))
        binding.rvOptions.layoutManager = LinearLayoutManager(this)

        optionSettingsAdapter = OptionSettingsAdapter(viewModel, this, this)
        binding.rvOptions.adapter = optionSettingsAdapter
        viewModel.getSettingsOptionList()

        if (Utilities.checkBiometricSupport(this)) {
            binding.cardBiometric.visibility = View.VISIBLE
        } else {
            binding.cardBiometric.visibility = View.GONE
        }

        binding.swBiometric.isChecked = viewModel.isBiometricAuthentication()

        binding.swBiometric.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBiometricAuthentication(isChecked)
            Utilities.toastMessageShort(
                this,
                "${resources.getString(R.string.BIOMETRIC_AUTHENTICATION)} ${
                    if (isChecked) resources.getString(R.string.ENABLED)
                    else resources.getString(R.string.DISABLED)
                }"
            )
        }

        viewModel.personDelete.observe(this) {
            viewModel.hideProgressBar()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(600)
                if (it.status == Resource.Status.SUCCESS) {
                    if (!Utilities.isNullOrEmptyOrZero(it.data!!.accountID)) {
                        CleverTapHelper.pushEvent(this@SettingsActivity, CleverTapConstants.DELETE_ACCOUNT)
                        Utilities.toastMessageShort(this@SettingsActivity, resources.getString(R.string.DELETE_ACCOUNT_SUCCESS))

                        backgroundCallViewModel.logoutFromDB()
                        if(Utilities.logout(this@SettingsActivity,this@SettingsActivity)) {
//                            openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                            finish()
                        }
                    }
                }
            }
        }

        viewModel.settingsOptionListData.observe(this) {
            it?.let {
                optionSettingsAdapter!!.updateDashboardOptionsList(it)
            }
        }
    }

    override fun onSettingsOptionListener(position: Int, option: DataHandler.Option) {
        Utilities.printLogError("SelectedPosition=>$position")
        when (option.code) {
            Constants.LANGUAGE -> {
                openAnotherActivity(destination = NavigationConstants.LANGUAGE_SCREEN) {
                    putString(Constants.FROM, "")
                }
            }

            Constants.CHANGE_PASSWORD -> {
                openAnotherActivity(destination = NavigationConstants.PASSWORD_CHANGE)
            }

            Constants.DELETE_ACCOUNT -> {
                showDialog(
                    listener = this,
                    title = this.resources.getString(R.string.DELETE_ACCOUNT_TITLE),
                    message = this.resources.getString(R.string.DELETE_ACCOUNT_CONFIRMATION),
                    leftText = this.resources.getString(R.string.CANCEL),
                    rightText = this.resources.getString(R.string.CONFIRM),
                    showLeftBtn = true,
                    hasErrorBtn = true
                )
            }

            /*            "RATE_US" -> {
                            Utilities.goToPlayStore(this)
                        }*/
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.SETTINGS)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
//        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true,animate = false)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            viewModel.callPersonDeleteApi()
        }
    }

    private fun invalidateAktivoData() {
        try {
            val aktivoManager = AktivoManager.getInstance(this)
            aktivoManager!!
                .invalidateUser(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
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

}