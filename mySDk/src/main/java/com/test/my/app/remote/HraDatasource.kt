package com.test.my.app.remote

import com.test.my.app.di.DIModule
import com.test.my.app.model.hra.BMIExistModel
import com.test.my.app.model.hra.BPExistModel
import com.test.my.app.model.hra.HraAssessmentSummaryModel
import com.test.my.app.model.hra.HraHistoryModel
import com.test.my.app.model.hra.HraListRecommendedTestsModel
import com.test.my.app.model.hra.HraMedicalProfileSummaryModel
import com.test.my.app.model.hra.HraStartModel
import com.test.my.app.model.hra.LabRecordsModel
import com.test.my.app.model.hra.SaveAndSubmitHraModel
import javax.inject.Inject
import javax.inject.Named

class HraDatasource @Inject constructor( @Named(DIModule.ENCRYPTED)  private val encryptedService: ApiService) {

    suspend fun getHraStartResponse(data: HraStartModel) = encryptedService.hraStartAPI(data)

    suspend fun getBMIExistResponse(data: BMIExistModel) = encryptedService.checkBMIExistAPI(data)

    suspend fun getBPExistResponse(data: BPExistModel) = encryptedService.checkBPExistAPI(data)

    suspend fun getLabRecordsResponse(data: LabRecordsModel) =
        encryptedService.fetchLabRecordsAPI(data)

    suspend fun getSaveAndSubmitHraResponse(data: SaveAndSubmitHraModel) =
        encryptedService.saveAndSubmitHraAPI(data)

    suspend fun getMedicalProfileSummary(data: HraMedicalProfileSummaryModel) =
        encryptedService.getMedicalProfileSummaryAPI(data)

    suspend fun getHraHistory(data: HraHistoryModel) = encryptedService.getHRAHistory(data)

    suspend fun getAssessmentSummary(data: HraAssessmentSummaryModel) =
        encryptedService.getAssessmentSummaryAPI(data)

    suspend fun getListRecommendedTests(data: HraListRecommendedTestsModel) =
        encryptedService.getListRecommendedTestsAPI(data)

}