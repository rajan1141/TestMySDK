package com.test.my.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.R
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Configuration
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.Event
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.home.common.HRAObservationModel
import com.test.my.app.home.common.SmitFitEventsService
import com.test.my.app.home.domain.AktivoManagementUseCase
import com.test.my.app.home.domain.BackgroundCallUseCase
import com.test.my.app.home.domain.HomeManagementUseCase
import com.test.my.app.home.domain.SudLifePolicyManagementUseCase
import com.test.my.app.home.ui.HRAData
import com.test.my.app.home.ui.HomeScreenNewFragment
import com.test.my.app.medication_tracker.domain.MedicationManagementUseCase
import com.test.my.app.model.aktivo.AktivoCreateUserModel
import com.test.my.app.model.aktivo.AktivoGetUserTokenModel
import com.test.my.app.model.entity.HRASummary
import com.test.my.app.model.home.AddFeatureAccessLog
import com.test.my.app.model.home.LiveSessionModel
import com.test.my.app.model.hra.HraMedicalProfileSummaryModel
import com.test.my.app.model.medication.MedicineListByDayModel
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import com.test.my.app.model.waterTracker.GetDailyWaterIntakeModel
import com.test.my.app.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class HomeViewModel @Inject constructor(
    application: Application,
    private val backgroundCallUseCase: BackgroundCallUseCase,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val sudLifePolicyManagementUseCase: SudLifePolicyManagementUseCase,
    private val aktivoManagementUseCase: AktivoManagementUseCase,
    private val medicationManagementUseCase: MedicationManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?) : BaseViewModel(application) {

    val preferenceUtil = preferenceUtils
    private val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    var hraDetails = MutableLiveData<HRASummary>()
    var hraObservationLiveData = MutableLiveData<HRAObservationModel>()
    val liveSessions = MutableLiveData<List<LiveSessionModel>>()

    private var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> = MutableLiveData()
    private val _medicalProfileSummary = MediatorLiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>()
    val medicalProfileSummary: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> get() = _medicalProfileSummary

    private var getDailyWaterIntakeSource: LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> = MutableLiveData()
    private val _getDailyWaterIntake = MediatorLiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>>()
    val getDailyWaterIntake: LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> get() = _getDailyWaterIntake

    private var medicineListByDaySource: LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> = MutableLiveData()
    private val _medicineListByDay = MediatorLiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>>()
    val medicineListByDay: LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> get() = _medicineListByDay

    private var aktivoCreateUserSource: LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> = MutableLiveData()
    private val _aktivoCreateUser = MediatorLiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>>()
    val aktivoCreateUser: LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> get() = _aktivoCreateUser

    private var aktivoGetUserTokenSource: LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> = MutableLiveData()
    private val _aktivoGetUserToken = MediatorLiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>>()
    val aktivoGetUserToken: LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> get() = _aktivoGetUserToken

    private var policyProductsSource: LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> = MutableLiveData()
    private val _policyProducts = MediatorLiveData<Resource<PolicyProductsModel.PolicyProductsResponse>>()
    val policyProducts: LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> get() = _policyProducts

    private var addFeatureAccessLogSource: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> = MutableLiveData()
    private val _addFeatureAccessLog = MediatorLiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>>()
    val addFeatureAccessLog: LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> get() = _addFeatureAccessLog

    fun callGetMedicalProfileSummary() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = HraMedicalProfileSummaryModel(Gson().toJson(
            HraMedicalProfileSummaryModel.JSONDataRequest(
                PersonID = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" })
            ),HraMedicalProfileSummaryModel.JSONDataRequest::class.java),preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(Dispatchers.IO) {
            medicalProfileSummarySource = backgroundCallUseCase.invokeMedicalProfileSummary(
                isForceRefresh = false,
                data = requestData,
                personId = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" })
            )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            try {
                _medicalProfileSummary.value = it
                if (it.status == Resource.Status.SUCCESS) {
                    Utilities.printLogError("MedicalProfileSummery=>Inside view model::  $it")
                    var wellnessScore = 0
                    val abc = it.data!!.MedicalProfileSummary!!.scorePercentile
                    if ( !Utilities.isNullOrEmptyOrZero(abc!!.toString()) ) {
                        wellnessScore = abc.toInt()
                        if (wellnessScore <= 0) {
                            wellnessScore = 0
                        }
                    }
                    val hraData = HashMap<String, Any>()
                    hraData[CleverTapConstants.HRA_SCORE] = wellnessScore
                    CleverTapHelper.pushEventWithProperties(context, CleverTapConstants.HRA_DATA_INFO,hraData)
                }
                if (it.status == Resource.Status.ERROR) {
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                        logoutFromDB()
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }
    }

    fun callGetDailyWaterIntakeApi(date: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GetDailyWaterIntakeModel(Gson().toJson(
                GetDailyWaterIntakeModel.JSONDataRequest(GetDailyWaterIntakeModel.Request(
                        personID = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" }).toInt(),
                        recordDate = date)),
                GetDailyWaterIntakeModel.JSONDataRequest::class.java),preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        //_progressBar.value = Event("")
        _getDailyWaterIntake.removeSource(getDailyWaterIntakeSource)
        withContext(Dispatchers.IO) {
            getDailyWaterIntakeSource = homeManagementUseCase.invokeGetDailyWaterIntake(
                isForceRefresh = true,
                data = requestData
            )
        }
        _getDailyWaterIntake.addSource(getDailyWaterIntakeSource) {
            _getDailyWaterIntake.value = it

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                    logoutFromDB()
                } else {
                    //toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callMedicineListByDayApi(medicationDate: String) = viewModelScope.launch(Dispatchers.Main) {
        try{
            val requestData = MedicineListByDayModel(
                Gson().toJson(MedicineListByDayModel.JSONDataRequest(
                personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                medicationDate = medicationDate), MedicineListByDayModel.JSONDataRequest::class.java), authToken = preferenceUtils.getPreference(
                    PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("Getting Medicines...")
            _medicineListByDay.removeSource(medicineListByDaySource)
            withContext(Dispatchers.IO) {
                medicineListByDaySource = medicationManagementUseCase.invokeGetMedicationListByDay(requestData)
            }
            _medicineListByDay.addSource(medicineListByDaySource) {
                _medicineListByDay.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                        logoutFromDB()
                    }
                    else {
                        toastMessage(it.errorMessage)
                    }
                    //fragment.stopShimmer()
                    //fragment.updateMedicinesList()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun callAktivoCreateUserApi(deviceToken: String) = viewModelScope.launch(Dispatchers.Main) {

        val objMember = JsonObject()
        objMember.addProperty("externalId", (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" }))
        objMember.addProperty("nickname", preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, ""))
        objMember.addProperty("date_of_birth", preferenceUtils.getPreference(PreferenceConstants.DOB, ""))
        objMember.addProperty("sex", Utilities.getDisplayGender(preferenceUtils.getPreference(PreferenceConstants.GENDER, "")))
        if (!Utilities.isNullOrEmpty(deviceToken)) {
            objMember.addProperty("deviceToken", deviceToken)
        }
/*        for (i in wellfieSingleton.getBmiVitalsList()) {
            when (i.parameterCode) {
                "HEIGHT" -> {
                    objMember.addProperty("height_cm", i.value!!.toInt().toString())
                }

                "WEIGHT" -> {
                    objMember.addProperty("weight_kg", i.value!!.toInt().toString())
                }
            }
        }*/

        val obj = JsonObject()
        obj.add("member", objMember)
        val requestData = AktivoCreateUserModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        //_progressBar.value = Event("")
        _aktivoCreateUser.removeSource(aktivoCreateUserSource)
        withContext(Dispatchers.IO) {
            aktivoCreateUserSource = aktivoManagementUseCase.invokeAktivoCreateUser(data = requestData)
        }
        _aktivoCreateUser.addSource(aktivoCreateUserSource) {
            _aktivoCreateUser.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //toastMessage(it.errorMessage)
            }
        }

    }

    fun callAktivoGetUserTokenApi(aktivoMemberId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("member_id", aktivoMemberId)
            val requestData = AktivoGetUserTokenModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            _aktivoGetUserToken.removeSource(aktivoGetUserTokenSource)
            withContext(Dispatchers.IO) {
                aktivoGetUserTokenSource =
                    aktivoManagementUseCase.invokeAktivoGetUserToken(data = requestData)
            }
            _aktivoGetUserToken.addSource(aktivoGetUserTokenSource) {
                _aktivoGetUserToken.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        Utilities.printLog("Response--->" + it.data)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    //toastMessage(it.errorMessage)
                }
            }

        }

    fun callPolicyProductsApi(fragment: HomeScreenNewFragment) = viewModelScope.launch(Dispatchers.Main) {

            val requestData = PolicyProductsModel(
                Gson().toJson(
                    PolicyProductsModel.JSONDataRequest(
                        screen = "DASHBOARD",
                        personID = (preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" }).toInt(),
                        accountId =( preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0").ifEmpty { "0" }).toInt(),
                        orgEmpID = preferenceUtils.getPreference(
                            PreferenceConstants.ORG_EMPLOYEE_ID,
                            ""
                        ),
                        orgName = preferenceUtils.getPreference(PreferenceConstants.ORG_NAME, ""),
                        userType = Utilities.getEmployeeType()
                    ),
                    PolicyProductsModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _policyProducts.removeSource(policyProductsSource)

            withContext(Dispatchers.IO) {
                policyProductsSource = sudLifePolicyManagementUseCase.invokePolicyProducts(data = requestData)
            }
            _policyProducts.addSource(policyProductsSource) {
                _policyProducts.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printData("PolicyProductsDashboard",it.data!!)
                        val list = it.data.policyProducts.toMutableList()
                        list.sortBy { it.productID }
                        fragment.policyBannerList.clear()
                        fragment.policyBannerList.addAll(list)
                        fragment.slidingDashboardAdapter!!.updatePolicyBannerList()
                        /*else {
                            fragment.hideBannerView()
                        }*/
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                    //fragment.stopDashboardProductsShimmer()
                    //fragment.hideBannerView()
                }
            }

        }

    fun callAddFeatureAccessLogApi(code: String, desc: String, service: String, url: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = AddFeatureAccessLog(
                Gson().toJson(
                    AddFeatureAccessLog.JSONDataRequest(
                        AddFeatureAccessLog.FeatureAccessLog(
                            partnerCode = Constants.PartnerCode,
                            personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0").ifEmpty { "0" }.toInt(),
                            code = code,
                            description = desc,
                            service = service,
                            url = url,
                            appversion = Utilities.getVersionName(context!!),
                            device = Build.BRAND + "~" + Build.MODEL,
                            devicetype = "Android",
                            platform = "App")),AddFeatureAccessLog.JSONDataRequest::class.java
                ),preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            _addFeatureAccessLog.removeSource(addFeatureAccessLogSource)
            withContext(Dispatchers.IO) {
                addFeatureAccessLogSource = homeManagementUseCase.invokeAddFeatureAccessLog(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _addFeatureAccessLog.addSource(addFeatureAccessLogSource) {
                _addFeatureAccessLog.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        if (!Utilities.isNullOrEmptyOrZero(it.data.featureAccessLogID)) {
                            //toastMessage("Count Saved")
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callSmitFitEventsApi() {

        //_progressBar.value = Event("")
        val smitFitEventsService = provideRetrofit("https://apiv2.smit.fit/").create(SmitFitEventsService::class.java)
        val call = smitFitEventsService.getSmitFitEventsApi()

        call.enqueue(object : Callback<List<LiveSessionModel>> {
            override fun onResponse(
                call: Call<List<LiveSessionModel>>,
                response: Response<List<LiveSessionModel>>
            ) {
                if (response.body() != null) {
                    val result = response.body()!!
                    if (response.isSuccessful) {
                        Utilities.printData("SmitFitEvents", result, true)
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                liveSessions.postValue(result)
                            }
                        }
                    } else {
                        Utilities.printLogError("Server Contact failed")
                    }
                } else {
                    Utilities.printLogError("response.body is null")
                }
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            override fun onFailure(call: Call<List<LiveSessionModel>>, t: Throwable) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Utilities.printLog("Api failed" + t.printStackTrace())
            }
        })
    }

    private fun provideRetrofit(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .addInterceptor(logging)
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .build()
            )
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun setHraSummaryDetails(hraSummary: HRASummary) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            hraDetails.postValue(hraSummary)
            //hraDetails.postValue(homeManagementUseCase.invokeGetHraSummaryDetails())
        }
    }

    fun setupHRAWidgetData(hraSummary: HRASummary?) {
        try {
            var hraObservation = 0
            var color = R.color.dia_ichi_grey_dark
            var observation = localResource.getString(R.string.TAKE_YOUR_HRA)
            if (hraSummary!!.hraCutOff == "1") {
                hraObservation = hraSummary.scorePercentile.toInt()
            } else {
                hraObservation = 0
                observation = localResource.getString(R.string.TAKE_YOUR_HRA)
            }

            if (hraSummary != null) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }

                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals("0", ignoreCase = true)) {
                    when {
                        hraCutOff.equals("0", ignoreCase = true) -> {
                            observation = localResource.getString(R.string.TAKE_YOUR_HRA)
                            color = R.color.dia_ichi_grey_dark
                        }

                        wellnessScore in 0..15 -> {
                            observation = localResource.getString(R.string.HIGH_RISK)
                            color = R.color.risk_high
                        }

                        wellnessScore in 16..45 -> {
                            observation = localResource.getString(R.string.MODERATE_RISK)
                            color = R.color.risk_moderate
                        }

                        wellnessScore in 46..85 -> {
                            observation = localResource.getString(R.string.HEALTHY)
                            color = R.color.risk_healthy
                        }

                        wellnessScore > 85 -> {
                            observation = localResource.getString(R.string.OPTIMUM)
                            color = R.color.risk_optimum

                        }
                    }
                } else {
                    color = R.color.dark_gray
                    observation = localResource.getString(R.string.TAKE_YOUR_HRA)
                }
            } else {
                color = R.color.dark_gray
                observation = localResource.getString(R.string.TAKE_YOUR_HRA)
            }
            HRAData.data = HRAObservationModel(color, observation, hraObservation)
            hraObservationLiveData.postValue(HRAObservationModel(color, observation, hraObservation))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUserPreference(key: String): String {
        val userPreference = preferenceUtils.getPreference(key)
        Utilities.printLogError("$key--->$userPreference")
        return userPreference
    }

    fun storeUserPreference(key: String, value: String) {
        Utilities.printLogError("Storing $key--->$value")
        preferenceUtils.storePreference(key, value)
    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

    fun logoutFromDB() = viewModelScope.launch(Dispatchers.Main) {
        Configuration.EntityID = "0"
        withContext(Dispatchers.IO) {
            backgroundCallUseCase.invokeLogout()
        }
    }
}