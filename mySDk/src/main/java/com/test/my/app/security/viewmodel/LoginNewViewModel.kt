package com.test.my.app.security.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.Event
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.entity.Users
import com.test.my.app.model.home.AddFeatureAccessLog
import com.test.my.app.model.security.EmailExistsModel
import com.test.my.app.model.security.GenerateOtpModel
import com.test.my.app.model.security.PhoneExistsModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.security.domain.UserManagementUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashMap
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class LoginNewViewModel @Inject constructor(
    application: Application,
    private val userManagementUseCase: UserManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    private lateinit var argsLogin: String

    private var isEmailExistGoogleSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> = MutableLiveData()
    private val _isEmailGoogle = MediatorLiveData<Resource<EmailExistsModel.IsExistResponse>>()
    val isEmailGoogle: LiveData<Resource<EmailExistsModel.IsExistResponse>> get() = _isEmailGoogle

    private var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()
    private val _isPhone = MediatorLiveData<Resource<PhoneExistsModel.IsExistResponse>>()
    val isPhone: LiveData<Resource<PhoneExistsModel.IsExistResponse>> get() = _isPhone

    private var otpGenerateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpGenerateData = MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpGenerateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpGenerateData

    private var otpValidateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpValidateData = MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpValidateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpValidateData

    private var loginUserSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _isLogin = MediatorLiveData<Users>()
    val isLogin: LiveData<Users> get() = _isLogin

    private var addFeatureAccessLogSource: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> = MutableLiveData()
    private val _addFeatureAccessLog = MediatorLiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>()
    val addFeatureAccessLog: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> get() = _addFeatureAccessLog
/*
    fun checkEmailExistOrNotGoogle(name: String = "",
                                   email: String,
                                   fragment: LoginFragment) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = EmailExistsModel(Gson().toJson(
                EmailExistsModel.JSONDataRequest(
                    emailAddress = email), EmailExistsModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Email..")
        _isEmailGoogle.removeSource(isEmailExistGoogleSource)
        withContext(Dispatchers.IO) {
            isEmailExistGoogleSource = userManagementUseCase.invokeEmailExist(true, requestData)
        }
        _isEmailGoogle.addSource(isEmailExistGoogleSource) {
            _isEmailGoogle.value = it

            if (it.status == Resource.Status.SUCCESS) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                it?.data?.let { data ->
                    fragment.isExist = data.isExist.equals(Constants.TRUE, true)
                    fragment.loginType = Constants.EMAIL
                    if (fragment.isExist) {
                        fragment.viewModel.callLogin(
                            emailStr = data.account!!.emailAddress!!,
                            mobileStr = data.account!!.primaryPhone!!,
                            passwordStr = "",
                            socialLogin = true,
                            source = Constants.GOOGLE_SOURCE,
                            view = fragment.binding.btnGetOtp)
                    } else {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        fragment.phone = ""
                        fragment.showNotRegisteredDialog(Constants.EMAIL, email)
                    }
                } ?: run {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }
                *//*                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                        callLogin(
                            name = name,
                            emailStr = email,
                            passwordStr = "",
                            socialLogin = true,
                            source = Constants.GOOGLE_SOURCE,
                            view = view)
                    } else {
                        toastMessage(localResource.getString(R.string.ERROR_EMAIL_NOT_REGISTERED))
                    }*//*
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun checkPhoneExistAPI(phoneNumber: String = "",
                           fragment: LoginFragment) = viewModelScope.launch(Dispatchers.Main) {

            _progressBar.value = Event("")
            val requestData = PhoneExistsModel(Gson().toJson(
                    PhoneExistsModel.JSONDataRequest(
                        primaryPhone = phoneNumber),PhoneExistsModel.JSONDataRequest::class.java))

            _isPhone.removeSource(phoneSource)
            withContext(Dispatchers.IO) {
                phoneSource = userManagementUseCase.invokePhoneExist(true, requestData)
            }
            _isPhone.addSource(phoneSource) {
                _isPhone.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    it?.data?.let { data ->
                        fragment.isExist = data.isExist.equals(Constants.TRUE, true)
                        fragment.loginType = Constants.PHONE
                        //fragment.phone = fragment.binding.edtLoginEmailPhone.text.toString().trim()
                        fragment.phone = phoneNumber
                        if (fragment.isExist) {
                            fragment.email = data.account!!.emailAddress!!
                            fragment.viewModel.callGenerateVerificationCode(
                                "",
                                phone = phoneNumber,
                                fragment)
                        } else {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            fragment.showNotRegisteredDialog(Constants.PHONE, phoneNumber)
                        }
                    } ?: run {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)

                    }
                    *//*                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                        callGenerateVerificationCode(email = it.data?.account!!.emailAddress!!, phone = phoneNumber)
                    } else {
                        callGenerateVerificationCode(email = "", phone = phoneNumber)
                        toastMessage(localResource.getString(R.string.ERROR_PHONE_NOT_REGISTERED))
                    }*//*
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    fun callGenerateVerificationCode(email: String,
                                     phone: String = "",
                                     fragment: LoginFragment) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    GenerateOtpModel.UPN(
                        //loginName = email,
                        //emailAddress = email,
                        primaryPhone = phone
                    ), message = "Generating OTP"),GenerateOtpModel.JSONDataRequest::class.java))

        //_progressBar.value = Event("Generating OTP")
        _otpGenerateData.removeSource(otpGenerateSource)
        withContext(Dispatchers.IO) {
            otpGenerateSource = userManagementUseCase.invokeGenerateOTP(true, requestData)
        }
        _otpGenerateData.addSource(otpGenerateSource) {
            _otpGenerateData.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                    //toastMessage(localResource.getString(R.string.MSG_VERIFICATION_CODE_SENT))
                    if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                        Utilities.printLogError("IsBottomSheetOpen--->${fragment.isBottomSheetOpen}")
                        if (!fragment.isBottomSheetOpen) {
                            fragment.showBottomSheet()
                        } else {
                            fragment.modalBottomSheetOTP!!.refreshTimer()
                        }
                        fragment.startSmsUserConsent()
                    }
                } else {
                    toastMessage(it.errorMessage)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //toastMessage(it.errorMessage)
            }
        }
    }

    fun callValidateVerificationCode(otpReceived: String,
                                     email: String = "",
                                     phone: String = "", fragment: LoginFragment) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    upn = GenerateOtpModel.UPN(primaryPhone = phone),
                    otp = otpReceived,
                    message = "Verifing Code..."),GenerateOtpModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Verifing Code...")
        _otpValidateData.removeSource(otpValidateSource)
        withContext(Dispatchers.IO) {
            otpValidateSource = userManagementUseCase.invokeValidateOTP(true, requestData)
        }
        _otpValidateData.addSource(otpValidateSource) {
            _otpValidateData.value = it

            if (it.status == Resource.Status.SUCCESS) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                it?.data?.let { data ->
                    if (data.validity.equals(Constants.TRUE, true)) {
                        fragment.modalBottomSheetOTP!!.otpTimer.cancel()
                        fragment.modalBottomSheetOTP!!.dismiss()
                        fragment.loginOrProceedRegistration()
                    } else {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        Utilities.toastMessageShort(context, localResource.getString(R.string.ERROR_INVALID_OTP))
                    }
                } ?: run {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    Utilities.printLog("callValidateVerificationCode--->${it.data}")
                }*//*                    callLogin(
                                    forceRefresh = true,
                                    name = name,
                                    emailStr = emailStr,
                                    passwordStr = passwordStr,
                                    socialLogin = socialLogin,
                                    source = source,
                                    socialId = socialId,
                                    view = view)*//**//*                    callLogin(
                                    forceRefresh = true,
                                    emailStr = it.data!!.account!!.emailAddress.toString(),
                                    passwordStr = passwordStr,
                                    view = view)*//*
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callLogin(
        name: String = "",
        emailStr: String,
        mobileStr: String = "",
        passwordStr: String = "",
        socialLogin: Boolean = false,
        source: String = Constants.LOGIN_SOURCE,
        view: View) = viewModelScope.launch(Dispatchers.Main) {

        argsLogin = Utilities.getEncryptedData(
            email = emailStr,
            password = passwordStr,
            name = emailStr,
            source = source,
            isSocial = socialLogin)

        //_progressBar.value = Event("Authenticating..")
        _isLogin.removeSource(loginUserSource) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(Dispatchers.IO) {
            loginUserSource = userManagementUseCase(isForceRefresh = true, data = argsLogin)
        }
        _isLogin.addSource(loginUserSource) {
            it?.data?.let {data->
                _isLogin.value = data
            }

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                try {
                    it?.data?.let { getData ->
                        Utilities.printLog("LoginResp--->$getData")
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
                        preferenceUtils.storePreference(PreferenceConstants.EMAIL, getData.emailAddress)
                        preferenceUtils.storePreference(PreferenceConstants.PHONE, getData.phoneNumber)
                        preferenceUtils.storePreference(PreferenceConstants.TOKEN, getData.authToken)
                        preferenceUtils.storePreference(PreferenceConstants.ACCOUNTID, getData.accountId.toDouble().toInt().toString())
                        preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, getData.firstName)
                        preferenceUtils.storePreference(PreferenceConstants.PROFILE_IMAGE_ID, getData.profileImageID.toString())
                        preferenceUtils.storePreference(PreferenceConstants.GENDER, getData.gender)
                        preferenceUtils.storePreference(PreferenceConstants.ORG_NAME,getData.orgName)
                        preferenceUtils.storePreference(PreferenceConstants.ORG_EMPLOYEE_ID,getData.orgEmpID)
                        val pid = getData.personId.toDouble().toInt()
                        Utilities.printLog("Person Id => $pid")
                        preferenceUtils.storePreference(PreferenceConstants.PERSONID, pid.toString())
                        preferenceUtils.storePreference(PreferenceConstants.ADMIN_PERSON_ID, pid.toString())
                        preferenceUtils.storePreference(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE)
                        preferenceUtils.storePreference(PreferenceConstants.JOINING_DATE, getData.createdDate!!.split("T").toTypedArray()[0])
                        preferenceUtils.storePreference(PreferenceConstants.DOB, getData.dateOfBirth!!.split("T").toTypedArray()[0])
                        val bundle = Bundle()
                        when (source) {
                            Constants.LOGIN_SOURCE -> {
                                bundle.putString(Constants.FROM, Constants.LOGIN_WITH_OTP)
                                preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, true)
                                preferenceUtils.storePreference(PreferenceConstants.POLICY_MOBILE_NUMBER, getData.phoneNumber)
                            }
                            Constants.GOOGLE_SOURCE -> {
                                bundle.putString(Constants.FROM, Constants.LOGIN)
                            }
                        }
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, false)
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, true)
                        CleverTapHelper.addUser(context)
                        loginCleverTapEvent(mobileStr,emailStr,source,Constants.SUCCESS)
                        if (preferenceUtils.getBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE)) {
                            val referralName = preferenceUtils.getPreference(CleverTapConstants.REFERRAL_NAME)
                            val referralPID = preferenceUtils.getPreference(CleverTapConstants.REFERRAL_PID)
                            val data = HashMap<String, Any>()
                            data[CleverTapConstants.REFERRAL_NAME] = referralName
                            data[CleverTapConstants.REFERRAL_PID] = referralPID
                            CleverTapHelper.pushEventWithProperties(context, CleverTapConstants.LOGIN_BY_REFERRAL, data)
                            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE, false)
                            //AddFeatureAccessLog
                            val desc = "ReferralName:$referralName|ReferralPID:$referralPID|PersonID:$pid"
                            callAddFeatureAccessLogApi(pid, Constants.LOGIN_BY_REFERRAL, desc)
                        }
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_main_activity,bundle)
                    } ?: run {
                        Utilities.printLog("LoginResp--->${it.data}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
                loginCleverTapEvent(mobileStr,emailStr,source,Constants.FAILURE)
            }
        }
    }*/

    private fun loginCleverTapEvent(phone:String,email:String,source:String,status:String) {
        val customData = HashMap<String,Any>()
        when(status) {
            Constants.SUCCESS -> customData[CleverTapConstants.STATUS] = Constants.SUCCESS
            Constants.FAILURE -> customData[CleverTapConstants.STATUS] = Constants.FAILURE
        }
        when (source) {
            Constants.LOGIN_SOURCE -> {
                customData[CleverTapConstants.PHONE] = phone
                CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.LOGIN_WITH_OTP,customData)
            }
            Constants.GOOGLE_SOURCE -> {
                customData[CleverTapConstants.EMAIL] = email
                CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.LOGIN_WITH_GOOGLE,customData)
            }
        }
    }

    private fun callAddFeatureAccessLogApi(personId: Int, code: String, desc: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = AddFeatureAccessLog(Gson().toJson(
                AddFeatureAccessLog.JSONDataRequest(
                    AddFeatureAccessLog.FeatureAccessLog(
                            partnerCode = Constants.PartnerCode,
                            personId = personId,
                            code = code,
                            description = desc,
                            service = Constants.REFERRAL,
                            url = "",
                            appversion = Utilities.getVersionName(context!!),
                            device = Build.BRAND + "~" + Build.MODEL,
                            devicetype = "Android",
                            platform = "App")
                    ),AddFeatureAccessLog.JSONDataRequest::class.java
                ),preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            _addFeatureAccessLog.removeSource(addFeatureAccessLogSource)
            withContext(Dispatchers.IO) {
                addFeatureAccessLogSource = userManagementUseCase.invokeAddFeatureAccessLog(isForceRefresh = true, data = requestData)
            }
            _addFeatureAccessLog.addSource(addFeatureAccessLogSource) {
                _addFeatureAccessLog.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        if (!Utilities.isNullOrEmptyOrZero(it.data!!.featureAccessLogID)) {
                            //toastMessage("Count Saved")
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }
}