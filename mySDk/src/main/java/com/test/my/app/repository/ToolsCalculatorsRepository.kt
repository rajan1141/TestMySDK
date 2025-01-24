package com.test.my.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.test.my.app.model.toolscalculators.DiabetesSaveResponceModel
import com.test.my.app.model.toolscalculators.HeartAgeSaveResponceModel
import com.test.my.app.model.toolscalculators.HypertensionSaveResponceModel
import com.test.my.app.model.toolscalculators.SmartPhoneSaveResponceModel
import com.test.my.app.model.toolscalculators.StartQuizModel
import com.test.my.app.model.toolscalculators.StressAndAnxietySaveResponceModel
import com.test.my.app.remote.ToolsCalculatorsDatasource
import com.test.my.app.repository.utils.NetworkBoundResource
import com.test.my.app.repository.utils.Resource
import com.test.my.app.model.BaseResponse
import javax.inject.Inject

interface ToolsCalculatorsRepository {

    suspend fun startQuiz(
        forceRefresh: Boolean = false,
        data: StartQuizModel
    ): LiveData<Resource<StartQuizModel.StartQuizResponse>>

    suspend fun heartAgeSaveResponce(
        forceRefresh: Boolean = false,
        data: HeartAgeSaveResponceModel
    ): LiveData<Resource<HeartAgeSaveResponceModel.HeartAgeSaveResponce>>

    suspend fun diabetesSaveResponce(
        forceRefresh: Boolean = false,
        data: DiabetesSaveResponceModel
    ): LiveData<Resource<DiabetesSaveResponceModel.DiabetesSaveResponce>>

    suspend fun hypertensionSaveResponce(
        forceRefresh: Boolean = false,
        data: HypertensionSaveResponceModel
    ): LiveData<Resource<HypertensionSaveResponceModel.HypertensionSaveResponce>>

    suspend fun stressAndAnxietySaveResponce(
        forceRefresh: Boolean = false,
        data: StressAndAnxietySaveResponceModel
    ): LiveData<Resource<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>>

    suspend fun smartPhoneSaveResponce(
        forceRefresh: Boolean = false,
        data: SmartPhoneSaveResponceModel
    ): LiveData<Resource<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>>
}

class ToolsCalculatorsRepositoryImpl @Inject constructor(
    private val datasource: ToolsCalculatorsDatasource,
    val context: Context
) : ToolsCalculatorsRepository {

    override suspend fun startQuiz(
        forceRefresh: Boolean,
        data: StartQuizModel
    ): LiveData<Resource<StartQuizModel.StartQuizResponse>> {

        return object :
            NetworkBoundResource<StartQuizModel.StartQuizResponse, BaseResponse<StartQuizModel.StartQuizResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): StartQuizModel.StartQuizResponse {
                return StartQuizModel.StartQuizResponse()
            }

            override fun processResponse(response: BaseResponse<StartQuizModel.StartQuizResponse>): StartQuizModel.StartQuizResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<StartQuizModel.StartQuizResponse> {
                return datasource.getStartQuizResponse(data)
            }

            override suspend fun saveCallResults(items: StartQuizModel.StartQuizResponse) {}

            override fun shouldFetch(data: StartQuizModel.StartQuizResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun heartAgeSaveResponce(
        forceRefresh: Boolean,
        data: HeartAgeSaveResponceModel
    ): LiveData<Resource<HeartAgeSaveResponceModel.HeartAgeSaveResponce>> {

        return object :
            NetworkBoundResource<HeartAgeSaveResponceModel.HeartAgeSaveResponce, BaseResponse<HeartAgeSaveResponceModel.HeartAgeSaveResponce>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): HeartAgeSaveResponceModel.HeartAgeSaveResponce {
                return HeartAgeSaveResponceModel.HeartAgeSaveResponce()
            }

            override fun processResponse(response: BaseResponse<HeartAgeSaveResponceModel.HeartAgeSaveResponce>): HeartAgeSaveResponceModel.HeartAgeSaveResponce {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<HeartAgeSaveResponceModel.HeartAgeSaveResponce> {
                return datasource.getHeartAgeSaveResponce(data)
            }

            override suspend fun saveCallResults(items: HeartAgeSaveResponceModel.HeartAgeSaveResponce) {}

            override fun shouldFetch(data: HeartAgeSaveResponceModel.HeartAgeSaveResponce?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun diabetesSaveResponce(
        forceRefresh: Boolean,
        data: DiabetesSaveResponceModel
    ): LiveData<Resource<DiabetesSaveResponceModel.DiabetesSaveResponce>> {

        return object :
            NetworkBoundResource<DiabetesSaveResponceModel.DiabetesSaveResponce, BaseResponse<DiabetesSaveResponceModel.DiabetesSaveResponce>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): DiabetesSaveResponceModel.DiabetesSaveResponce {
                return DiabetesSaveResponceModel.DiabetesSaveResponce()
            }

            override fun processResponse(response: BaseResponse<DiabetesSaveResponceModel.DiabetesSaveResponce>): DiabetesSaveResponceModel.DiabetesSaveResponce {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<DiabetesSaveResponceModel.DiabetesSaveResponce> {
                return datasource.getDiabetesSaveResponce(data)
            }

            override suspend fun saveCallResults(items: DiabetesSaveResponceModel.DiabetesSaveResponce) {}

            override fun shouldFetch(data: DiabetesSaveResponceModel.DiabetesSaveResponce?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun hypertensionSaveResponce(
        forceRefresh: Boolean,
        data: HypertensionSaveResponceModel
    ): LiveData<Resource<HypertensionSaveResponceModel.HypertensionSaveResponce>> {

        return object :
            NetworkBoundResource<HypertensionSaveResponceModel.HypertensionSaveResponce, BaseResponse<HypertensionSaveResponceModel.HypertensionSaveResponce>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): HypertensionSaveResponceModel.HypertensionSaveResponce {
                return HypertensionSaveResponceModel.HypertensionSaveResponce()
            }

            override fun processResponse(response: BaseResponse<HypertensionSaveResponceModel.HypertensionSaveResponce>): HypertensionSaveResponceModel.HypertensionSaveResponce {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<HypertensionSaveResponceModel.HypertensionSaveResponce> {
                return datasource.getHypertensionSaveResponce(data)
            }

            override suspend fun saveCallResults(items: HypertensionSaveResponceModel.HypertensionSaveResponce) {}

            override fun shouldFetch(data: HypertensionSaveResponceModel.HypertensionSaveResponce?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun stressAndAnxietySaveResponce(
        forceRefresh: Boolean,
        data: StressAndAnxietySaveResponceModel
    ): LiveData<Resource<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>> {

        return object :
            NetworkBoundResource<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse, BaseResponse<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse {
                return StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse()
            }

            override fun processResponse(response: BaseResponse<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>): StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse> {
                return datasource.getStressAndAnxietySaveResponce(data)
            }

            override suspend fun saveCallResults(items: StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse) {}

            override fun shouldFetch(data: StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun smartPhoneSaveResponce(
        forceRefresh: Boolean,
        data: SmartPhoneSaveResponceModel
    ): LiveData<Resource<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>> {

        return object :
            NetworkBoundResource<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce, BaseResponse<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SmartPhoneSaveResponceModel.SmartPhoneSaveResponce {
                return SmartPhoneSaveResponceModel.SmartPhoneSaveResponce()
            }

            override fun processResponse(response: BaseResponse<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>): SmartPhoneSaveResponceModel.SmartPhoneSaveResponce {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce> {
                return datasource.getSmartPhoneSaveResponce(data)
            }

            override suspend fun saveCallResults(items: SmartPhoneSaveResponceModel.SmartPhoneSaveResponce) {}

            override fun shouldFetch(data: SmartPhoneSaveResponceModel.SmartPhoneSaveResponce?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

}