package com.test.my.app.security.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.test.my.app.model.entity.Users
import com.test.my.app.model.home.AddFeatureAccessLog
import com.test.my.app.model.home.UpdateUserDetailsModel
import com.test.my.app.model.home.UploadProfileImageResponce
import com.test.my.app.model.security.ChangePasswordModel
import com.test.my.app.model.security.EmailExistsModel
import com.test.my.app.model.security.GenerateOtpModel
import com.test.my.app.model.security.LoginNameExistsModel
import com.test.my.app.model.security.PhoneExistsModel
import com.test.my.app.model.security.TermsConditionsModel
import com.test.my.app.repository.HomeRepository
import com.test.my.app.repository.UserRepository
import com.test.my.app.repository.utils.Resource
import okhttp3.RequestBody
import javax.inject.Inject

class UserManagementUseCase @Inject constructor(
    private val repository: UserRepository,
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(
        isForceRefresh: Boolean,
        data: String,
        isOtpAuthenticated: Boolean = false
    ): LiveData<Resource<Users>> {
        return repository.getLoginResponse(isForceRefresh, data, isOtpAuthenticated)
    }

    suspend fun invokeRegistration(
        data: String,
        isOtpAuthenticated: Boolean = false
    ): LiveData<Resource<Users>> {
        return repository.fetchRegistrationResponse(data, isOtpAuthenticated)
    }

    suspend fun invokeEmailExist(
        isForceRefresh: Boolean,
        data: EmailExistsModel
    ): LiveData<Resource<EmailExistsModel.IsExistResponse>> {
        return repository.isEmailExist(isForceRefresh, data)
    }

    suspend fun invokePhoneExist(
        isForceRefresh: Boolean,
        data: PhoneExistsModel
    ): LiveData<Resource<PhoneExistsModel.IsExistResponse>> {
        return repository.isPhoneExist(isForceRefresh, data)
    }

    suspend fun invokeGenerateOTP(
        isForceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return repository.getGenerateOTPResponse(isForceRefresh, data)
    }

    suspend fun invokeValidateOTP(
        isForceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return repository.getValidateOTPResponse(isForceRefresh, data)
    }

    suspend fun invokeUpdatePassword(
        isForceRefresh: Boolean,
        data: ChangePasswordModel
    ): LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>> {
        return repository.updatePassword(isForceRefresh, data)
    }

    suspend fun invokeTermsCondition(
        isForceRefresh: Boolean,
        data: TermsConditionsModel
    ): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> {
        return repository.getTermsConditionsResponse(isForceRefresh, data)
    }

    // New Api Integration

    suspend fun invokeLoginNameExist(
        isForceRefresh: Boolean,
        data: LoginNameExistsModel
    ): LiveData<Resource<LoginNameExistsModel.IsExistResponse>> {
        return repository.isLoginNameExist(isForceRefresh, data)
    }

    suspend fun invokeAddUserInfo(data: Users) {
        repository.saveUserInfo(data)

    }

    suspend fun invokeUploadProfileImage(
        personID: RequestBody, fileName: RequestBody, documentTypeCode: RequestBody,
        byteArray: RequestBody, authTicket: RequestBody
    ): LiveData<Resource<UploadProfileImageResponce>> {
        return homeRepository.uploadProfileImage(
            personID,
            fileName,
            documentTypeCode,
            byteArray,
            authTicket
        ).map {
            it
        }
    }

    suspend fun invokeUpdateUserProfileImgPath(name: String, path: String) {
        return homeRepository.updateUserProfileImgPath(name, path)
    }

    suspend fun invokeUpdateUserDetails(isForceRefresh: Boolean, data: UpdateUserDetailsModel):
            LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {
        return homeRepository.updateUserDetails(isForceRefresh, data)
    }

    suspend fun invokeAddFeatureAccessLog(
        isForceRefresh: Boolean,
        data: AddFeatureAccessLog
    ): LiveData<Resource<AddFeatureAccessLog.AddFeatureAccessLogResponse>> {
        return homeRepository.addFeatureAccessLog(isForceRefresh, data)
    }
}