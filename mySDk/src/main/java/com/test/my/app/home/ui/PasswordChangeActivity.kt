package com.test.my.app.home.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
/*import com.aktivolabs.aktivocore.data.models.User
import com.aktivolabs.aktivocore.data.repositories.LocalRepository
import com.aktivolabs.aktivocore.managers.AktivoManager*/
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.DefaultNotificationDialog
import com.test.my.app.common.utils.DialogSuccess
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.utils.Validation
import com.test.my.app.common.utils.showDialog
import com.test.my.app.databinding.ActivityPasswordChangeBinding
import com.test.my.app.home.viewmodel.BackgroundCallViewModel
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class PasswordChangeActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: ActivityPasswordChangeBinding

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val backgroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }

    private val appColorHelper = AppColorHelper.instance!!
    private var btnReLogin: Button? = null
    private var dialogPasswordUpdated: DialogSuccess? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_password_change)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        initialise()
        setClickable()
    }

    private fun initialise() {

        dialogPasswordUpdated = DialogSuccess(this)
        dialogPasswordUpdated!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnReLogin = dialogPasswordUpdated!!.findViewById(R.id.btn_go_to_app)

        btnReLogin!!.text = resources.getString(R.string.RE_LOGIN)

        viewModel.passwordChange.observe(this) {}
    }

    private fun setClickable() {

        binding.imgPasswordInfo.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.PASSWORD_CRITERIA),
                message = "<a>" + "- ${resources.getString(R.string.PASSWORD_CRITERIA_DESC1)} <br/><br/> - ${
                    resources.getString(
                        R.string.PASSWORD_CRITERIA_DESC2
                    )
                } <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC3)} <br/> \t\t - ${
                    resources.getString(
                        R.string.PASSWORD_CRITERIA_DESC4
                    )
                } <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC5)} <br/> \t\t - ${
                    resources.getString(
                        R.string.PASSWORD_CRITERIA_DESC6
                    )
                }" + "</a>",
                showLeftBtn = false
            )
        }

        binding.btnChangePassword.setOnClickListener {
            validate()
            //showPasswordUpdatedDialog()
        }

        btnReLogin!!.setOnClickListener {
            dialogPasswordUpdated!!.dismiss()
            backgroundCallViewModel.logoutFromDB()
            if(Utilities.logout(this,this)) {
//                openAnotherActivity(destination = NavigationConstants.LOGIN, clearTop = true)
                finish()
            }
        }

    }

    private fun validate() {

        val oldPassword: String = binding.edtOldPassword.text.toString()
        val newPassword: String = binding.edtNewPassword.text.toString()
        val confirmPassword: String = binding.edtConfirmPassword.text.toString()

        if (Utilities.isNullOrEmpty(oldPassword)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.VALIDATE_EMPTY_OLD_PASSWORD)
            )
        } else if (Utilities.isNullOrEmpty(newPassword)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.VALIDATE_EMPTY_NEW_PASSWORD)
            )
        } else if (Utilities.isNullOrEmpty(confirmPassword)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.VALIDATE_EMPTY_CONFIRM_PASSWORD)
            )
        } else if (!Validation.isValidPassword(newPassword)) {
            Utilities.toastMessageShort(this, resources.getString(R.string.VALIDATE_NEW_PASSWORD))
        } else if (!Validation.isValidPassword(confirmPassword)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.VALIDATE_CONFIRM_PASSWORD)
            )
        } else if (oldPassword.equals(newPassword, ignoreCase = true)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.ERROR_NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_OLD_PASSWORD)
            )
        } else if (oldPassword.equals(confirmPassword, ignoreCase = true)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.ERROR_NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_OLD_PASSWORD)
            )
        } else if (!newPassword.equals(confirmPassword, ignoreCase = true)) {
            Utilities.toastMessageShort(
                this,
                resources.getString(R.string.ERROR_PASSWORD_DOES_NOT_MATCH)
            )
        } else {
            viewModel.callPasswordChangeApi(oldPassword, confirmPassword, this)
            //showPasswordUpdatedDialog()
        }

    }

    fun showPasswordUpdatedDialog() {
        CleverTapHelper.pushEvent(this, CleverTapConstants.CHANGE_PASSWORD)
        dialogPasswordUpdated!!.show()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = ""
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

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

    /*private fun invalidateAktivoData() {
        try {
            val localRepository = LocalRepository(this)
            val aktivoManager = AktivoManager.getInstance(this)
            aktivoManager!!
                .invalidateUser(User(localRepository.userId), this)
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
    }*/

}