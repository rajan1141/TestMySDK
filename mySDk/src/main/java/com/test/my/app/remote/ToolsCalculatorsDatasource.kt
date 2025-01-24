package com.test.my.app.remote

import com.test.my.app.di.DIModule
import com.test.my.app.model.toolscalculators.DiabetesSaveResponceModel
import com.test.my.app.model.toolscalculators.HeartAgeSaveResponceModel
import com.test.my.app.model.toolscalculators.HypertensionSaveResponceModel
import com.test.my.app.model.toolscalculators.SmartPhoneSaveResponceModel
import com.test.my.app.model.toolscalculators.StartQuizModel
import com.test.my.app.model.toolscalculators.StressAndAnxietySaveResponceModel
import javax.inject.Inject
import javax.inject.Named

class ToolsCalculatorsDatasource @Inject constructor(@Named(DIModule.ENCRYPTED) private val encryptedService: ApiService) {

    suspend fun getStartQuizResponse(data: StartQuizModel) =
        encryptedService.toolsStartQuizApi(data)

    suspend fun getHeartAgeSaveResponce(data: HeartAgeSaveResponceModel) =
        encryptedService.toolsHeartAgeSaveResponceApi(data)

    suspend fun getDiabetesSaveResponce(data: DiabetesSaveResponceModel) =
        encryptedService.toolsDiabetesSaveResponceApi(data)

    suspend fun getHypertensionSaveResponce(data: HypertensionSaveResponceModel) =
        encryptedService.toolsHypertensionSaveResponceApi(data)

    suspend fun getStressAndAnxietySaveResponce(data: StressAndAnxietySaveResponceModel) =
        encryptedService.toolsStressAndAnxietySaveResponceApi(data)

    suspend fun getSmartPhoneSaveResponce(data: SmartPhoneSaveResponceModel) =
        encryptedService.toolsSmartPhoneSaveResponceApi(data)


}