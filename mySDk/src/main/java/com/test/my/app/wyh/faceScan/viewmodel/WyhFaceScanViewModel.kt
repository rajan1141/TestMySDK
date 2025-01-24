package com.test.my.app.wyh.faceScan.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.R
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.CalculateParameters
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Event
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.home.domain.HomeManagementUseCase
import com.test.my.app.model.hra.BMIExistModel
import com.test.my.app.model.parameter.SaveParameterModel
import com.test.my.app.model.wyh.WyhAuthorizationModel
import com.test.my.app.model.wyh.faceScan.AddFaceScanVitalsModel
import com.test.my.app.model.wyh.faceScan.GetFaceScanVitalsModel
import com.test.my.app.model.wyh.faceScan.GetSocketKeysModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.domain.WyhManagementUseCase
import com.test.my.app.wyh.faceScan.ui.BeginVitalsActivity
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WyhFaceScanViewModel@Inject constructor(
    application: Application,
    private val wyhManagementUseCase: WyhManagementUseCase,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?) : BaseViewModel(application) {

    private val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    var age = DateHelper.calculatePersonAge(preferenceUtils.getPreference(PreferenceConstants.DOB, ""), context!!)
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    //val wyhToken = preferenceUtils.getPreference(PreferenceConstants.WYH_ACCESS_TOKEN, "")

    private var getWyhAuthorizationSource: LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>> = MutableLiveData()
    private val _getWyhAuthorization = MediatorLiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>>()
    val getWyhAuthorization: LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>> get() = _getWyhAuthorization

    private var getSocketKeysSource: LiveData<Resource<GetSocketKeysModel.GetSocketKeysResponse>> = MutableLiveData()
    private val _getSocketKeys = MediatorLiveData<Resource<GetSocketKeysModel.GetSocketKeysResponse>>()
    val getSocketKeys: LiveData<Resource<GetSocketKeysModel.GetSocketKeysResponse>> get() = _getSocketKeys

    private var addFaceScanVitalsSource: LiveData<Resource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse>> = MutableLiveData()
    private val _addFaceScanVitals = MediatorLiveData<Resource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse>>()
    val addFaceScanVitals: LiveData<Resource<AddFaceScanVitalsModel.AddFaceScanVitalsResponse>> get() = _addFaceScanVitals

    private var getFaceScanVitalsSource: LiveData<Resource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse>> = MutableLiveData()
    private val _getFaceScanVitals = MediatorLiveData<Resource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse>>()
    val getFaceScanVitals: LiveData<Resource<GetFaceScanVitalsModel.GetFaceScanVitalsResponse>> get() = _getFaceScanVitals

    private var checkBmiSource: LiveData<Resource<BMIExistModel.BMIExistResponse>> = MutableLiveData()
    private val _bmiDetails = MediatorLiveData<Resource<BMIExistModel.BMIExistResponse>>()
    val bmiDetails: LiveData<Resource<BMIExistModel.BMIExistResponse>> get() = _bmiDetails

    private var saveParamUserSource: LiveData<Resource<SaveParameterModel.Response>> = MutableLiveData()
    private val _saveParam = MediatorLiveData<Resource<SaveParameterModel.Response>>()
    val saveParam: LiveData<Resource<SaveParameterModel.Response>> get() = _saveParam

    fun callGetWyhAuthorizationApi() = viewModelScope.launch(Dispatchers.Main) {

        val phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")

        //_progressBar.value = Event("")
        _getWyhAuthorization.removeSource(getWyhAuthorizationSource)
        withContext(Dispatchers.IO) {
            getWyhAuthorizationSource = wyhManagementUseCase.invokeGetWyhAuthorization(mobile = phone)
        }
        _getWyhAuthorization.addSource(getWyhAuthorizationSource) {
            _getWyhAuthorization.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    //Utilities.printLog("Response--->" + it.data)
                    Utilities.toastMessageShort(context,"Token Updated")
                    //storeUserPreference(PreferenceConstants.WYH_ACCESS_TOKEN,it.data.data!!)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callGetSocketKeysApi() = viewModelScope.launch(Dispatchers.Main) {

        _progressBar.value = Event("")
        _getSocketKeys.removeSource(getSocketKeysSource)
        withContext(Dispatchers.IO) {
            getSocketKeysSource = wyhManagementUseCase.invokeGetSocketKeys()
        }
        _getSocketKeys.addSource(getSocketKeysSource) {
            _getSocketKeys.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    //Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callAddFaceScanVitalsApi(request : AddFaceScanVitalsModel.AddFaceScanVitalsRequest) = viewModelScope.launch(Dispatchers.Main) {

        _progressBar.value = Event("")
        _addFaceScanVitals.removeSource(addFaceScanVitalsSource)
        withContext(Dispatchers.IO) {
            addFaceScanVitalsSource = wyhManagementUseCase.invokeAddFaceScanVitals( request = request )
        }
        _addFaceScanVitals.addSource(addFaceScanVitalsSource) {
            _addFaceScanVitals.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    //Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callGetFaceScanVitalsApi() = viewModelScope.launch(Dispatchers.Main) {

        //_progressBar.value = Event("")
        _getFaceScanVitals.removeSource(getFaceScanVitalsSource)
        withContext(Dispatchers.IO) {
            getFaceScanVitalsSource = wyhManagementUseCase.invokeGetFaceScanVitals()
        }
        _getFaceScanVitals.addSource(getFaceScanVitalsSource) {
            _getFaceScanVitals.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    //Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callIsBMIExist() = viewModelScope.launch(Dispatchers.Main) {
            try {
                val requestData = BMIExistModel(Gson().toJson(BMIExistModel.JSONDataRequest(
                    PersonID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")),
                    BMIExistModel.JSONDataRequest::class.java), preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

                _progressBar.value = Event("")
                _bmiDetails.removeSource(checkBmiSource)
                withContext(Dispatchers.IO) {
                    checkBmiSource = homeManagementUseCase.invokeBMIExist(isForceRefresh = true, data = requestData)
                }
                _bmiDetails.addSource(checkBmiSource) {
                    _bmiDetails.value = it
                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            //Utilities.printLog("Response--->" + it.data)
                        }
                    }
                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun saveParameter(
        activity: BeginVitalsActivity,
        height: String,
        weight: String,
        socketToken: String) = viewModelScope.launch(Dispatchers.Main) {

            val personId = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" })
        val bmi = String.format("%.1f", CalculateParameters.roundOffPrecision(CalculateParameters.getBMI(height.toDouble(), weight.toDouble()), 1))
        val recordList = arrayListOf<SaveParameterModel.Record>()

        recordList.add(SaveParameterModel.Record(
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "HEIGHT",
                personId,
                "BMI",
                DateHelper.currentDateAsStringddMMMyyyy,
                "cm",
                height,
                ""))

        recordList.add(SaveParameterModel.Record(
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "WEIGHT",
                personId,
                "BMI",
                DateHelper.currentDateAsStringddMMMyyyy,
                "kg",
                weight,
                ""))

        recordList.add(
            SaveParameterModel.Record(
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                personId,
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "BMI",
                personId,
                "BMI",
                DateHelper.currentDateAsStringddMMMyyyy,
                "cm",
                bmi,
                ""
            )
        )

        val requestData = SaveParameterModel(
            Gson().toJson(
                SaveParameterModel.JSONDataRequest(recordList),
                SaveParameterModel.JSONDataRequest::class.java
            ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("Saving Parameter values...")
        _saveParam.removeSource(saveParamUserSource)
        withContext(Dispatchers.IO) {
            saveParamUserSource = homeManagementUseCase.invokeSaveParameter(data = requestData)
        }
        _saveParam.addSource(saveParamUserSource) {
            _saveParam.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //Utilities.toastMessageShort(context,"Vitals Updated")
                activity.navigateToFaceScan(socketToken)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun getFaceScanStatusMsg(code: String,apiMsg: String): String {
        val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
        val msg = when(code) {
            "CALIBRATING" -> localResource.getString(R.string.STATUS_CALIBRATING)
            "NOISE_DURING_EXECUTION" -> localResource.getString(R.string.STATUS_NOISE_DURING_EXECUTION)
            "RECALIBRATING" -> localResource.getString(R.string.STATUS_RECALIBRATING)
            "NO_FACE" -> localResource.getString(R.string.STATUS_NO_FACE)
            "FACE_LOST" -> localResource.getString(R.string.STATUS_FACE_LOST)
            "BRIGHT_LIGHT_ISSUE" -> localResource.getString(R.string.STATUS_BRIGHT_LIGHT_ISSUE)
            "UNKNOWN" -> localResource.getString(R.string.STATUS_UNKNOWN)
            "SUCCESS" -> localResource.getString(R.string.STATUS_SUCCESS)
            "MOVING_WARNING" -> localResource.getString(R.string.STATUS_MOVING_WARNING)
            //"UNSTABLE_CONDITIONS_WARNING" -> localResource.getString(R.string.STATUS_UNSTABLE_CONDITIONS_WARNING)
            //"INTERFERENCE_WARNING" -> localResource.getString(R.string.STATUS_INTERFERENCE_WARNING)
            else -> apiMsg
        }
        return msg
    }

    fun storeUserPreference(key: String, value: String) {
        Utilities.printLogError("Storing $key--->$value")
        preferenceUtils.storePreference(key, value)
    }

    fun getPreference(key: String): String {
        return preferenceUtils.getPreference(key, "")
    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

}