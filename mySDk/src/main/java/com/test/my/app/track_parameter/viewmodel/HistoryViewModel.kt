package com.test.my.app.track_parameter.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.PreferenceConstants
import com.test.my.app.common.utils.PreferenceUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.model.entity.TrackParameterMaster
import com.test.my.app.model.parameter.ParameterListModel
import com.test.my.app.model.parameter.ParameterProfile
import com.test.my.app.model.parameter.ParentProfileModel

import com.test.my.app.track_parameter.domain.ParameterManagementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCase: ParameterManagementUseCase,
    application: Application,
    private val preferenceUtils: PreferenceUtils
) : BaseViewModel(application) {

    private val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")

    var listAllProfiles = MutableLiveData<List<ParameterProfile>>()

    var paramSelectedSource: List<ParameterListModel.SelectedParameter> = listOf()
    private val _paramSelectedList = MediatorLiveData<ParameterListModel.SelectedParameter>()
    val paramSelectedList: LiveData<ParameterListModel.SelectedParameter> get() = _paramSelectedList

    private val _selectedParam = MediatorLiveData<List<ParameterListModel.SelectedParameter>>()
    val selectedParameter: LiveData<List<ParameterListModel.SelectedParameter>> get() = _selectedParam

    private val _savedParamList = MediatorLiveData<List<TrackParameterMaster.History>>()
    val savedParamList: LiveData<List<TrackParameterMaster.History>> get() = _savedParamList

    private val _observationList =
        MediatorLiveData<List<TrackParameterMaster.TrackParameterRanges>>()
    val observationList: LiveData<List<TrackParameterMaster.TrackParameterRanges>> get() = _observationList

    val paramHistory: MutableLiveData<List<TrackParameterMaster.History>> by lazy {
        MutableLiveData<List<TrackParameterMaster.History>>()
    }

    val paramBPHistory: MutableLiveData<Map<String, List<TrackParameterMaster.History>>> by lazy {
        MutableLiveData<Map<String, List<TrackParameterMaster.History>>>()
    }

    val spinnerHistoryLiveData: MutableLiveData<List<TrackParameterMaster.History>> by lazy {
        MutableLiveData<List<TrackParameterMaster.History>>()
    }

    fun getParameterHistory(parameterCode: String) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            val data = useCase.invokeParameterHisBaseOnCode(
                parameterCode,
                preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
            if (data.isNotEmpty()) paramHistory.postValue(data.reversed())
            else paramHistory.postValue(data)
        }
    }

    var listHistoryByProfileCodeAndMonthYear =
        MutableLiveData<ArrayList<TrackParameterMaster.History>>()
    var parentDataList = MutableLiveData<List<ParentProfileModel>>()

    fun getBPParameterHistory() = viewModelScope.launch(Dispatchers.Main) {
        val bpList: MutableMap<String, List<TrackParameterMaster.History>> = mutableMapOf()
        withContext(Dispatchers.IO) {
            val sysList = useCase.invokeParameterHisBaseOnCode(
                "BP_SYS",
                preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
            val diaList = useCase.invokeParameterHisBaseOnCode(
                "BP_DIA",
                preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
            if (sysList.isNotEmpty() && diaList.isNotEmpty()) {
                bpList.put("BP_SYS", sysList.reversed())
                bpList.put("BP_DIA", diaList.reversed())
            }
            paramBPHistory.postValue(bpList)
        }
    }

    fun getSpinnerData(profileCode: String) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            spinnerHistoryLiveData.postValue(
                useCase.invokeLatestParametersHisBaseOnProfileCode(
                    profileCode,
                    preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                )
            )

        }
    }

    fun getParentDataList(
        mapProfilesAll: MutableList<ParameterProfile>, month: String, year: String
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val parentProfilesList = ArrayList<ParentProfileModel>()
                val m = "%$month%"
                val y = "%$year%"
                Utilities.printLog("month,year--->$month,$year")
                for (i in mapProfilesAll.indices) {
                    val paramHistoryList: ArrayList<TrackParameterMaster.History> = arrayListOf()
                    paramHistoryList.addAll(
                        useCase.invokeListHistoryByProfileCodeAndMonthYear(
                            personId, mapProfilesAll[i].profileCode, m, y
                        )
                    )
                    if (paramHistoryList.isNotEmpty()) {
                        val parentMap = ParentProfileModel()
                        parentMap.profileCode = mapProfilesAll[i].profileCode
                        parentMap.profileName = mapProfilesAll[i].profileName
                        parentMap.childParameterList = paramHistoryList
                        parentProfilesList.add(parentMap)
                    }
                }
                parentDataList.postValue(parentProfilesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllProfileCodes() = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            listAllProfiles.postValue(useCase.invokeGetAllProfileCodes())
        }
    }

    fun parameterHistoryByProfileCode(profileCode: String) =
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val data = useCase.invokeLatestParametersHisBaseOnProfileCode(
                    profileCode,
                    preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                )
                _savedParamList.postValue(data)
            }
        }

    fun parameterObservationListByParameterCode(parameterCode: String) =
        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val data = useCase.invokeGetParameterObservationList(
                    parameterCode,
                    preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                )
                _observationList.postValue(data)
            }
        }

    private fun getFilterData(data: List<TrackParameterMaster.Parameter>?): List<ParameterListModel.InputParameterModel> {
        val list: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        for (item in data!!) {
            Utilities.printLog("DataItem=> " + item)
            val dataItem = ParameterListModel.InputParameterModel(
                parameterCode = item.code,
                parameterType = item.parameterType,
                description = item.description,
                profileCode = item.profileCode,
                profileName = item.profileName,
                parameterUnit = item.unit,
                minPermissibleValue = if (item.minPermissibleValue.isNullOrEmpty()) "" else item.minPermissibleValue,
                maxPermissibleValue = if (item.maxPermissibleValue.isNullOrEmpty()) "" else item.maxPermissibleValue,
                parameterTextVal = "",
                parameterVal = ""
            )
            list.add(dataItem)
        }
        return list
    }

    fun refreshSelectedParamList() = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            _selectedParam.postValue(
                useCase.invokeGetSelectParamList(
                    preferenceUtils.getPreference(
                        PreferenceConstants.SELECTION_PARAM,
                        ""
                    )
                )
            )
        }
    }

    fun navigateParam(action: NavDirections) {
        navigate(action)
    }

    /*    fun goToDetailHistory() {
            navigateParam(RevHistoryFragmentDirections.actionHistoryFragmentToDetailHistoryFragment())
        }*/

    fun updateSpinner() {

    }

}