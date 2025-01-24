package com.test.my.app.security.domain

import androidx.lifecycle.LiveData
import com.test.my.app.model.entity.Users
import com.test.my.app.model.home.CheckAppUpdateModel
import com.test.my.app.model.security.DarwinBoxDataModel
import com.test.my.app.repository.FitnessRepository
import com.test.my.app.repository.HomeRepository
import com.test.my.app.repository.HraRepository
import com.test.my.app.repository.MedicationRepository
import com.test.my.app.repository.ParameterRepository
import com.test.my.app.repository.StoreRecordRepository
import com.test.my.app.repository.UserRepository
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject

class StartupManagementUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val homeRepository: HomeRepository,
    private val hraRepository: HraRepository,
    private val shrRepository: StoreRecordRepository,
    private val trackParamRepo: ParameterRepository,
    private val medicationRepository: MedicationRepository,
    private val fitnessRepository: FitnessRepository) {

    suspend fun invokeSso(data: String): LiveData<Resource<Users>> {
        return userRepository.fetchSsoResponse(data)
    }

    suspend fun invokeLogout() {
        trackParamRepo.logoutUser()
        hraRepository.logoutUser()
        medicationRepository.logoutUser()
        shrRepository.logoutUser()
        homeRepository.logoutUser()
        fitnessRepository.logoutUser()
    }

    suspend fun invokeCheckAppUpdate(
        isForceRefresh: Boolean,
        data: CheckAppUpdateModel
    ): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> {
        return homeRepository.checkAppUpdate(isForceRefresh, data)
    }

    suspend fun invokeDarwinBoxDataResponse(
        isForceRefresh: Boolean,
        data: DarwinBoxDataModel
    ): LiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>> {
        return userRepository.darwinBoxDataResponse(isForceRefresh, data)
    }



}