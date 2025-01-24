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
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class RegistrationViewModel @Inject constructor(
    application: Application,
    private val userManagementUseCase: UserManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?) : BaseViewModel(application) {

    private val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!

    private var isEmailExistSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> = MutableLiveData()
    private val _isEmail = MediatorLiveData<Resource<EmailExistsModel.IsExistResponse>>()
    val isEmail: LiveData<Resource<EmailExistsModel.IsExistResponse>> get() = _isEmail

    private var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()
    private val _isPhone = MediatorLiveData<Resource<PhoneExistsModel.IsExistResponse>>()
    val isPhone: LiveData<Resource<PhoneExistsModel.IsExistResponse>> get() = _isPhone

    private var otpGenerateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpGenerateData = MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpGenerateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpGenerateData

    private var otpValidateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpValidateData = MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpValidateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpValidateData

    private var registerUserSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _isRegister = MediatorLiveData<Users>()
    val isRegister: LiveData<Users> get() = _isRegister

    private var addFeatureAccessLogSource: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> = MutableLiveData()
    private val _addFeatureAccessLog = MediatorLiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>()
    val addFeatureAccessLog: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> get() = _addFeatureAccessLog
/*
    fun checkEmailExistOrNot(email: String, fragment: SignUpStep1Fragment, view: View) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = EmailExistsModel(Gson().toJson(
                EmailExistsModel.JSONDataRequest(
                    emailAddress = email),EmailExistsModel.JSONDataRequest::class.java))

            _progressBar.value = Event("Validating Email..")
            _isEmail.removeSource(isEmailExistSource)
            withContext(Dispatchers.IO) {
                isEmailExistSource = userManagementUseCase.invokeEmailExist(true, requestData)
            }
            _isEmail.addSource(isEmailExistSource) {
                _isEmail.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.isExist.equals(Constants.TRUE, true)) {
                        Utilities.toastMessageShort(context, localResource.getString(R.string.ERROR_EMAIL_REGISTERED))
                    } else {
                        fragment.navigateToStepTwo()
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    fun checkPhoneExistAPI(phoneNumber: String,
                           fragment: SignUpStep1Fragment, view: View) = viewModelScope.launch(Dispatchers.Main) {

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
                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    Utilities.toastMessageShort(context, localResource.getString(R.string.ERROR_PHONE_REGISTERED))
                } else {
                    fragment.viewModel.callGenerateVerificationCode("", phoneNumber, fragment)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callGenerateVerificationCode(email: String,
                                     phone: String = "",
                                     fragment: SignUpStep1Fragment) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    GenerateOtpModel.UPN(
                        //loginName = email,
                        //emailAddress = email,
                        primaryPhone = phone
                    ), message = "Generating OTP"), GenerateOtpModel.JSONDataRequest::class.java))

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
                            fragment.modalBottomSheetOTPSignup!!.refreshTimer()
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
                                     email: String = "", phone: String = "",
                                     fragment: SignUpStep1Fragment) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    upn = GenerateOtpModel.UPN(primaryPhone = phone),
                    otp = otpReceived,
                    message = "Verifing Code..."), GenerateOtpModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Verifing Code...")
        _otpValidateData.removeSource(otpValidateSource)
        withContext(Dispatchers.IO) {
            otpValidateSource = userManagementUseCase.invokeValidateOTP(true, requestData)
        }
        _otpValidateData.addSource(otpValidateSource) {
            _otpValidateData.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.validity.equals(Constants.TRUE, true)) {
                    fragment.modalBottomSheetOTPSignup!!.otpTimer.cancel()
                    fragment.modalBottomSheetOTPSignup!!.dismiss()
                    fragment.navigateToStepTwo()
                } else {
                    Utilities.toastMessageShort(context, localResource.getString(R.string.ERROR_INVALID_OTP))
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callRegisterAPI(
        firstName: String = "",
        //lastName: String = "",
        emailStr: String,
        passwordStr: String = "",
        phoneNumber: String = "",
        dateOfBirthCal: Calendar = Calendar.getInstance(),
        socialLogin: Boolean = false,
        source: String = Constants.LOGIN_SOURCE,
        socialId: String = "",
        dob: String = "",
        gender: String = "1",
        userType: String = "",
        view: View) = viewModelScope.launch(Dispatchers.Main) {

        val registerEnc = Utilities.getEncryptedData(
            email = emailStr,
            password = passwordStr,
            dob = dob,
            phoneNumber = phoneNumber,
            name = firstName,
            isSocial = socialLogin,
            source = source,
            gender = gender
        )

        _progressBar.value = Event("Authentication User")
        _isRegister.removeSource(registerUserSource)
        withContext(Dispatchers.IO) {
            registerUserSource = userManagementUseCase.invokeRegistration(data = registerEnc, isOtpAuthenticated = true)
        }
        _isRegister.addSource(registerUserSource) {
            try {
                it?.data?.let { data ->
                    _isRegister.value = data
                }

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    it?.data?.let { getData->
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_LOGIN, true)
                        preferenceUtils.storePreference(PreferenceConstants.EMAIL, getData.emailAddress)
                        preferenceUtils.storePreference(PreferenceConstants.PHONE, getData.phoneNumber)
                        preferenceUtils.storePreference(PreferenceConstants.TOKEN, getData.authToken)
                        preferenceUtils.storePreference(PreferenceConstants.ACCOUNTID, getData.accountId.toDouble().toInt().toString())
                        preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, getData.firstName)
                        preferenceUtils.storePreference(PreferenceConstants.PROFILE_IMAGE_ID, getData.profileImageID.toString())
                        preferenceUtils.storePreference(PreferenceConstants.GENDER, getData.gender)
                        val pid = getData.personId.toDouble().toInt()
                        Utilities.printLogError("Person Id => $pid")
                        preferenceUtils.storePreference(PreferenceConstants.PERSONID, pid.toString())
                        preferenceUtils.storePreference(PreferenceConstants.ADMIN_PERSON_ID, pid.toString())
                        preferenceUtils.storePreference(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE)
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, true)
                        preferenceUtils.storePreference(PreferenceConstants.POLICY_MOBILE_NUMBER, getData.phoneNumber)
                        var joiningDate = DateHelper.currentDateAsStringyyyyMMdd
                        if (!Utilities.isNullOrEmpty(getData.createdDate!!)) {
                            joiningDate = getData.createdDate!!.split("T").toTypedArray()[0]
                        }
                        preferenceUtils.storePreference(PreferenceConstants.JOINING_DATE, joiningDate)
                        preferenceUtils.storePreference(PreferenceConstants.DOB, getData.dateOfBirth!!.split("T").toTypedArray()[0])
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FIRST_VISIT, false)
                        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_BASEURL_CHANGED, true)
                        val bundle = Bundle()
                        bundle.putString(Constants.FROM, Constants.SIGN_UP_NEW)
                        bundle.putString(Constants.USER_TYPE, userType)
                        Utilities.setEmployeeType(userType)
                        //Utilities.logCleverTapEmployeeEventSignUp(context!!, userType)
                        CleverTapHelper.addUser(context)
                        loginCleverTapEvent(phoneNumber,Constants.SUCCESS)
                        if (preferenceUtils.getBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE)) {
                            val referralName = preferenceUtils.getPreference(CleverTapConstants.REFERRAL_NAME)
                            val referralPID = preferenceUtils.getPreference(CleverTapConstants.REFERRAL_PID)
                            val data = HashMap<String, Any>()
                            data[CleverTapConstants.REFERRAL_NAME] = referralName
                            data[CleverTapConstants.REFERRAL_PID] = referralPID
                            CleverTapHelper.pushEventWithProperties(context, CleverTapConstants.REGISTRATION_BY_REFERRAL, data)
                            preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_REFERRAL_DETAILS_AVAILABLE, false)
                            //AddFeatureAccessLog
                            val desc = "ReferralName:$referralName|ReferralPID:$referralPID|PersonID:$pid"
                            callAddFeatureAccessLogApi(pid, Constants.REGISTRATION_BY_REFERRAL, desc)
                        }
                        Navigation.findNavController(view).navigate(R.id.action_signUpStep3Fragment_to_main_activity, bundle)
                    }?:run {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        toastMessage(it.errorMessage)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                    loginCleverTapEvent(phoneNumber,Constants.FAILURE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }*/

    private fun loginCleverTapEvent(phone:String,status:String) {
        val customData = HashMap<String, Any>()
        customData[CleverTapConstants.PHONE] = phone
        when(status) {
            Constants.SUCCESS -> customData[CleverTapConstants.STATUS] = Constants.SUCCESS
            Constants.FAILURE -> customData[CleverTapConstants.STATUS] = Constants.FAILURE
        }
        CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.SIGN_UP,customData)
    }

    private fun callAddFeatureAccessLogApi(personId: Int, code: String, desc: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = AddFeatureAccessLog(
                Gson().toJson(
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
                            platform = "App"
                        )
                    ), AddFeatureAccessLog.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            _addFeatureAccessLog.removeSource(addFeatureAccessLogSource)
            withContext(Dispatchers.IO) {
                addFeatureAccessLogSource = userManagementUseCase.invokeAddFeatureAccessLog(isForceRefresh = true, data = requestData)
            }
            _addFeatureAccessLog.addSource(addFeatureAccessLogSource) {
                try {
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

}