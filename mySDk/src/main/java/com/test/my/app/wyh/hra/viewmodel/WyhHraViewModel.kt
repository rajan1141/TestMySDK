package com.test.my.app.wyh.hra.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Event
import com.test.my.app.common.utils.LocaleHelper
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.wyh.WyhAuthorizationModel
import com.test.my.app.model.wyh.hra.CreateConversationModel
import com.test.my.app.model.wyh.hra.GetHraAnalysisModel
import com.test.my.app.model.wyh.hra.GetHraAnswersModel
import com.test.my.app.model.wyh.hra.SaveHraAnalysisModel
import com.test.my.app.model.wyh.hra.SaveHraAnswersModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.domain.WyhManagementUseCase
import com.test.my.app.wyh.hra.ui.QuestionPagerActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WyhHraViewModel@Inject constructor(
    application: Application,
    private val wyhManagementUseCase: WyhManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?) : BaseViewModel(application) {

    private val localResource = LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    var age = DateHelper.calculatePersonAge(preferenceUtils.getPreference(PreferenceConstants.DOB, ""), context!!)
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    //val wyhToken = preferenceUtils.getPreference(PreferenceConstants.WYH_ACCESS_TOKEN, "")

    private var getWyhAuthorizationSource: LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>> = MutableLiveData()
    private val _getWyhAuthorization = MediatorLiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>>()
    val getWyhAuthorization: LiveData<Resource<WyhAuthorizationModel.WyhAuthorizationResponse>> get() = _getWyhAuthorization

    private var createHraConversationSource: LiveData<Resource<CreateConversationModel.CreateConversationResponse>> = MutableLiveData()
    private val _createHraConversation = MediatorLiveData<Resource<CreateConversationModel.CreateConversationResponse>>()
    val createHraConversation: LiveData<Resource<CreateConversationModel.CreateConversationResponse>> get() = _createHraConversation

    private var getHraAnswersSource: LiveData<Resource<GetHraAnswersModel.GetHraAnswersResponse>> = MutableLiveData()
    private val _getHraAnswers = MediatorLiveData<Resource<GetHraAnswersModel.GetHraAnswersResponse>>()
    val getHraAnswers: LiveData<Resource<GetHraAnswersModel.GetHraAnswersResponse>> get() = _getHraAnswers

    private var saveHraAnswersSource: LiveData<Resource<SaveHraAnswersModel.SaveHraAnswersResponse>> = MutableLiveData()
    private val _saveHraAnswers = MediatorLiveData<Resource<SaveHraAnswersModel.SaveHraAnswersResponse>>()
    val saveHraAnswers: LiveData<Resource<SaveHraAnswersModel.SaveHraAnswersResponse>> get() = _saveHraAnswers

    private var saveHraAnalysisSource: LiveData<Resource<SaveHraAnalysisModel.SaveHraAnalysisResponse>> = MutableLiveData()
    private val _saveHraAnalysis = MediatorLiveData<Resource<SaveHraAnalysisModel.SaveHraAnalysisResponse>>()
    val saveHraAnalysis: LiveData<Resource<SaveHraAnalysisModel.SaveHraAnalysisResponse>> get() = _saveHraAnalysis

    private var getHraAnalysisSource: LiveData<Resource<GetHraAnalysisModel.GetHraAnalysisResponse>> = MutableLiveData()
    private val _getHraAnalysis = MediatorLiveData<Resource<GetHraAnalysisModel.GetHraAnalysisResponse>>()
    val getHraAnalysis: LiveData<Resource<GetHraAnalysisModel.GetHraAnalysisResponse>> get() = _getHraAnalysis

    fun callGetWyhAuthorizationApi() = viewModelScope.launch(Dispatchers.Main) {

        val phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")

        _progressBar.value = Event("")
        _getWyhAuthorization.removeSource(getWyhAuthorizationSource)
        withContext(Dispatchers.IO) {
            getWyhAuthorizationSource = wyhManagementUseCase.invokeGetWyhAuthorization(mobile = phone)
        }
        _getWyhAuthorization.addSource(getWyhAuthorizationSource) {
            _getWyhAuthorization.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callCreateHraConversationApi() = viewModelScope.launch(Dispatchers.Main) {

        val request = CreateConversationModel.CreateConversationRequest(
            integrationId = Constants.WYH_HRA_INTEGRATION_ID
        )

        _progressBar.value = Event("")
        _createHraConversation.removeSource(createHraConversationSource)
        withContext(Dispatchers.IO) {
            createHraConversationSource = wyhManagementUseCase.invokeWhyCreateHraConversation( request = request )
        }
        _createHraConversation.addSource(createHraConversationSource) {
            _createHraConversation.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callGetHraAnswersApi(activity: QuestionPagerActivity) = viewModelScope.launch(Dispatchers.Main) {

        val request = GetHraAnswersModel.GetHraAnswersRequest(
            isComplete = 0,
            integrationId = Constants.WYH_HRA_INTEGRATION_ID,
            surveyVersion = "1.0.0"
        )

        _progressBar.value = Event("")
        _getHraAnswers.removeSource(getHraAnswersSource)
        withContext(Dispatchers.IO) {
            getHraAnswersSource = wyhManagementUseCase.invokeWhyGetHraAnswers( request = request )
        }
        _getHraAnswers.addSource(getHraAnswersSource) {
            _getHraAnswers.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    //Utilities.printLog("Response--->" + it.data)
                    //activity.setupQuestionsAdapter()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSaveHraAnswersApi() = viewModelScope.launch(Dispatchers.Main) {

        val request = SaveHraAnswersModel.SaveHraAnswersRequest(
            //integrationId = Constants.WYH_HRA_INTEGRATION_ID
        )

        _progressBar.value = Event("")
        _saveHraAnswers.removeSource(saveHraAnswersSource)
        withContext(Dispatchers.IO) {
            saveHraAnswersSource = wyhManagementUseCase.invokeWhySaveHraAnswers( request = request )
        }
        _saveHraAnswers.addSource(saveHraAnswersSource) {
            _saveHraAnswers.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSaveHraAnalysisApi() = viewModelScope.launch(Dispatchers.Main) {

        val request = SaveHraAnalysisModel.SaveHraAnalysisRequest(
            //integrationId = Constants.WYH_HRA_INTEGRATION_ID
        )

        _progressBar.value = Event("")
        _saveHraAnalysis.removeSource(saveHraAnalysisSource)
        withContext(Dispatchers.IO) {
            saveHraAnalysisSource = wyhManagementUseCase.invokeWhySaveHraAnalysis( request = request )
        }
        _saveHraAnalysis.addSource(saveHraAnalysisSource) {
            _saveHraAnalysis.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callGetHraAnalysisApi() = viewModelScope.launch(Dispatchers.Main) {

        val request = GetHraAnalysisModel.GetHraAnalysisRequest(
            integrationId = Constants.WYH_HRA_INTEGRATION_ID
        )

        _progressBar.value = Event("")
        _getHraAnalysis.removeSource(getHraAnalysisSource)
        withContext(Dispatchers.IO) {
            getHraAnalysisSource = wyhManagementUseCase.invokeWhyGetHraAnalysis( request = request )
        }
        _getHraAnalysis.addSource(getHraAnalysisSource) {
            _getHraAnalysis.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
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