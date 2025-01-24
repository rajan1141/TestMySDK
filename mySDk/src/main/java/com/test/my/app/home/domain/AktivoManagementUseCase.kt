package com.test.my.app.home.domain

import androidx.lifecycle.LiveData
import com.test.my.app.model.aktivo.AktivoCheckUserModel
import com.test.my.app.model.aktivo.AktivoCreateUserModel
import com.test.my.app.model.aktivo.AktivoGetRefreshTokenModel
import com.test.my.app.model.aktivo.AktivoGetUserModel
import com.test.my.app.model.aktivo.AktivoGetUserTokenModel
import com.test.my.app.repository.AktivoRepository
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject


class AktivoManagementUseCase @Inject constructor(private val aktivoRepository: AktivoRepository) {

    suspend fun invokeAktivoCheckUser(data: AktivoCheckUserModel): LiveData<Resource<AktivoCheckUserModel.AktivoCheckUserResponse>> {
        return aktivoRepository.aktivoCheckUser(data)
    }

    suspend fun invokeAktivoCreateUser(data: AktivoCreateUserModel): LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> {
        return aktivoRepository.aktivoCreateUser(data)
    }

    suspend fun invokeAktivoGetUserToken(data: AktivoGetUserTokenModel): LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> {
        return aktivoRepository.aktivoGetUserToken(data)
    }

    suspend fun invokeAktivoGetRefreshToken(data: AktivoGetRefreshTokenModel): LiveData<Resource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>> {
        return aktivoRepository.aktivoGetRefreshToken(data)
    }

    suspend fun invokeAktivoGetUser(data: AktivoGetUserModel): LiveData<Resource<AktivoGetUserModel.AktivoGetUserResponse>> {
        return aktivoRepository.aktivoGetUser(data)
    }

}