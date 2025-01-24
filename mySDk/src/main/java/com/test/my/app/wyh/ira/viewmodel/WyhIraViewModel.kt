package com.test.my.app.wyh.ira.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.utils.Event
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.model.wyh.ira.CreateIraConversationModel
import com.test.my.app.model.wyh.ira.GetIraAnswersModel
import com.test.my.app.model.wyh.ira.GetIraHistoryModel
import com.test.my.app.model.wyh.ira.SaveIraAnswersModel
import com.test.my.app.repository.utils.Resource
import com.test.my.app.wyh.domain.WyhManagementUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WyhIraViewModel@Inject constructor(
    application: Application,
    private val wyhManagementUseCase: WyhManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?) : BaseViewModel(application) {

    private var getIraHistorySource: LiveData<Resource<GetIraHistoryModel.GetIRAHistoryResponse>> = MutableLiveData()
    private val _getIraHistory = MediatorLiveData<Resource<GetIraHistoryModel.GetIRAHistoryResponse>>()
    val getIraHistory: LiveData<Resource<GetIraHistoryModel.GetIRAHistoryResponse>> get() = _getIraHistory

    private var iraCreateConversationSource: LiveData<Resource<CreateIraConversationModel.CreateIraConversationResponse>> = MutableLiveData()
    private val _iraCreateConversation = MediatorLiveData<Resource<CreateIraConversationModel.CreateIraConversationResponse>>()
    val iraCreateConversation: LiveData<Resource<CreateIraConversationModel.CreateIraConversationResponse>> get() = _iraCreateConversation

    private var getIraAnswersSource: LiveData<Resource<GetIraAnswersModel.GetIraAnswersResponse>> = MutableLiveData()
    private val _getIraAnswers = MediatorLiveData<Resource<GetIraAnswersModel.GetIraAnswersResponse>>()
    val getIraAnswers: LiveData<Resource<GetIraAnswersModel.GetIraAnswersResponse>> get() = _getIraAnswers

    private var saveIraAnswersSource: LiveData<Resource<SaveIraAnswersModel.SaveIraAnswersResponse>> = MutableLiveData()
    private val _saveIraAnswers = MediatorLiveData<Resource<SaveIraAnswersModel.SaveIraAnswersResponse>>()
    val saveIraAnswers: LiveData<Resource<SaveIraAnswersModel.SaveIraAnswersResponse>> get() = _saveIraAnswers

    fun callGetIraHistoryApi() = viewModelScope.launch(Dispatchers.Main) {
        try {
            val request = GetIraHistoryModel.GetIRAHistoryRequest(
                sortField = "createdAt",
                asc = false
            )

            _progressBar.value = Event("")
            _getIraHistory.removeSource(getIraHistorySource)
            withContext(Dispatchers.IO) {
                getIraHistorySource = wyhManagementUseCase.invokeWhyGetIRAHistory( request = request )
            }
            _getIraHistory.addSource(getIraHistorySource) {
                _getIraHistory.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    /*if ( it.data != null ) {
                        if ( it.data.success!! && it.data.data != null ) {

                        }
                    }*/
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    fun callIraCreateConversationApi(showProgress:Boolean=false) = viewModelScope.launch(Dispatchers.Main) {
        try {
            val request = CreateIraConversationModel.CreateIraConversationRequest()

            if ( showProgress ) {
                _progressBar.value = Event("")
            }
            _iraCreateConversation.removeSource(iraCreateConversationSource)
            withContext(Dispatchers.IO) {
                iraCreateConversationSource = wyhManagementUseCase.invokeWhyIraCreateConversation( request = request )
            }
            _iraCreateConversation.addSource(iraCreateConversationSource) {
                _iraCreateConversation.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    /*if ( it.data != null ) {
                        if ( it.data.success!! && it.data.data != null ) {

                        }
                    }*/
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    fun callGetIraAnswersApi(conversationId:String) = viewModelScope.launch(Dispatchers.Main) {
        try {
            val request = GetIraAnswersModel.GetIraAnswersRequest(
                conversationId = conversationId
            )
            _progressBar.value = Event("")
            _getIraAnswers.removeSource(getIraAnswersSource)
            withContext(Dispatchers.IO) {
                getIraAnswersSource = wyhManagementUseCase.invokeWhyGetIraAnswers( request = request )
            }
            _getIraAnswers.addSource(getIraAnswersSource) {
                _getIraAnswers.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    /*if ( it.data != null ) {
                        if ( it.data.success!! && it.data.data != null ) {

                        }
                    }*/
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    fun callSaveIraAnswersApi(conversationId:String, answers:MutableMap<String,String>, questions:MutableList<GetIraAnswersModel.Question>) = viewModelScope.launch(Dispatchers.Main) {
        try {
            for (( key,value ) in answers) {
                for ( i in questions ) {
                    if ( i.questionId == key ) {
                        i.answer.clear()
                        i.answer.add(value)
                    }
                }
            }

            val request = SaveIraAnswersModel.SaveIraAnswersRequest(
                conversationId = conversationId,
                answerJson = Gson().toJson(questions) )

            _progressBar.value = Event("")
            _saveIraAnswers.removeSource(saveIraAnswersSource)
            withContext(Dispatchers.IO) {
                saveIraAnswersSource = wyhManagementUseCase.invokeWhySaveIraAnswers( request = request )
            }
            _saveIraAnswers.addSource(saveIraAnswersSource) {
                _saveIraAnswers.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    /*if ( it.data != null ) {
                        if ( it.data.success!! && it.data.data != null ) {

                        }
                    }*/
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

}