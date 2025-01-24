package com.test.my.app.water_tracker.domain

import androidx.lifecycle.LiveData
import com.test.my.app.model.hra.HraMedicalProfileSummaryModel
import com.test.my.app.model.waterTracker.GetDailyWaterIntakeModel
import com.test.my.app.model.waterTracker.GetWaterIntakeHistoryByDateModel
import com.test.my.app.model.waterTracker.GetWaterIntakeSummaryModel
import com.test.my.app.model.waterTracker.SaveDailyWaterIntakeModel
import com.test.my.app.model.waterTracker.SaveWaterIntakeGoalModel
import com.test.my.app.repository.HraRepository
import com.test.my.app.repository.WaterTrackerRepository
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject

class WaterTrackerManagementUseCase @Inject constructor(
    private val waterTrackerRepository: WaterTrackerRepository,
    private val hraRepository: HraRepository
) {

    suspend fun invokeSaveWaterIntakeGoal(
        isForceRefresh: Boolean,
        data: SaveWaterIntakeGoalModel
    ): LiveData<Resource<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>> {

        return waterTrackerRepository.saveWaterIntakeGoal(data)
    }

    suspend fun invokeSaveDailyWaterIntake(
        isForceRefresh: Boolean,
        data: SaveDailyWaterIntakeModel
    ): LiveData<Resource<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>> {

        return waterTrackerRepository.saveDailyWaterIntake(data)
    }

    suspend fun invokeGetDailyWaterIntake(
        isForceRefresh: Boolean,
        data: GetDailyWaterIntakeModel
    ): LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> {

        return waterTrackerRepository.getDailyWaterIntake(data)
    }

    suspend fun invokeGetWaterIntakeHistoryByDate(
        isForceRefresh: Boolean,
        data: GetWaterIntakeHistoryByDateModel
    ): LiveData<Resource<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>> {

        return waterTrackerRepository.getWaterIntakeHistoryByDate(data)
    }

    suspend fun invokeGetWaterIntakeSummary(
        isForceRefresh: Boolean,
        data: GetWaterIntakeSummaryModel
    ): LiveData<Resource<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>> {

        return waterTrackerRepository.getWaterIntakeSummary(data)
    }

    suspend fun invokeMedicalProfileSummary(
        isForceRefresh: Boolean,
        data: HraMedicalProfileSummaryModel,
        personId: String
    ): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return hraRepository.getMedicalProfileSummary(isForceRefresh, data, personId)
    }

}