package com.test.my.app.remote

import com.test.my.app.di.DIModule
import com.test.my.app.model.parameter.BMIHistoryModel
import com.test.my.app.model.parameter.BloodPressureHistoryModel
import com.test.my.app.model.parameter.LabRecordsListModel
import com.test.my.app.model.parameter.ParameterListModel
import com.test.my.app.model.parameter.ParameterPreferenceModel
import com.test.my.app.model.parameter.SaveParameterModel
import com.test.my.app.model.parameter.VitalsHistoryModel
import com.test.my.app.model.parameter.WHRHistoryModel
import javax.inject.Inject
import javax.inject.Named

class ParameterDatasource @Inject constructor(@Named(DIModule.ENCRYPTED)  private val encryptedService: ApiService) {

    suspend fun fetchParamList(data: ParameterListModel) = encryptedService.fetchParamList(data)

    suspend fun fetchLabRecordsList(data: LabRecordsListModel) =
        encryptedService.getLabRecordList(data)

    suspend fun fetchLabRecordsVitalsList(data: VitalsHistoryModel) =
        encryptedService.getLabRecordListVitals(data)

    suspend fun getParameterPreferences(data: ParameterPreferenceModel) =
        encryptedService.getParameterPreferences(data)

    suspend fun getBMIHistory(data: BMIHistoryModel) = encryptedService.getBMIHistory(data)

    suspend fun getWHRHistory(data: WHRHistoryModel) = encryptedService.getWHRHistory(data)

    suspend fun getBloodPressureHistory(data: BloodPressureHistoryModel) =
        encryptedService.getBloodPressureHistory(data)

    suspend fun addTrackParameter(data: SaveParameterModel) = encryptedService.saveLabRecords(data)
}