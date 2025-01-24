package com.test.my.app.home.domain

//import androidx.lifecycle.Transformations
import androidx.lifecycle.LiveData
import com.test.my.app.model.security.GenerateOtpModel
import com.test.my.app.model.sudLifePolicy.PolicyProductsModel
import com.test.my.app.model.sudLifePolicy.SudCreditLifeCOIModel
import com.test.my.app.model.sudLifePolicy.SudFundDetailsModel
import com.test.my.app.model.sudLifePolicy.SudGroupCOIModel
import com.test.my.app.model.sudLifePolicy.SudKYPModel
import com.test.my.app.model.sudLifePolicy.SudKypPdfModel
import com.test.my.app.model.sudLifePolicy.SudKypTemplateModel
import com.test.my.app.model.sudLifePolicy.SudPMJJBYCoiBaseModel
import com.test.my.app.model.sudLifePolicy.SudPayPremiumModel
import com.test.my.app.model.sudLifePolicy.SudPolicyByMobileNumberModel
import com.test.my.app.model.sudLifePolicy.SudPolicyDetailsByPolicyNumberModel
import com.test.my.app.model.sudLifePolicy.SudReceiptDetailsModel
import com.test.my.app.model.sudLifePolicy.SudRenewalPremiumModel
import com.test.my.app.model.sudLifePolicy.SudRenewalPremiumReceiptModel
import com.test.my.app.repository.SudLifePolicyRepository
import com.test.my.app.repository.UserRepository
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject


class SudLifePolicyManagementUseCase @Inject constructor(
    private val sudLifePolicyRepository: SudLifePolicyRepository,
    private val userRepository: UserRepository
) {

    suspend fun invokePolicyProducts(data: PolicyProductsModel): LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> {

        return sudLifePolicyRepository.policyProducts(data)
    }

    suspend fun invokeSudPolicyByMobileNumber(data: SudPolicyByMobileNumberModel): LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> {

        return sudLifePolicyRepository.sudPolicyByMobileNumber(data)
    }

    suspend fun invokeSudPolicyDetailsByPolicyNumber(data: SudPolicyDetailsByPolicyNumberModel): LiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>> {

        return sudLifePolicyRepository.sudPolicyDetailsByPolicyNumber(data)
    }

    suspend fun invokeSudKYP(data: SudKYPModel): LiveData<Resource<SudKYPModel.SudKYPResponse>> {

        return sudLifePolicyRepository.sudKYP(data)
    }

    suspend fun invokeSudGroupCoi(data: SudGroupCOIModel): LiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>> {

        return sudLifePolicyRepository.sudGroupCoiApi(data)
    }

    suspend fun invokeSudGetFundDetail(data: SudFundDetailsModel): LiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>> {

        return sudLifePolicyRepository.sudGetFundDetail(data)
    }

    suspend fun invokeSudGetReceiptDetails(data: SudReceiptDetailsModel): LiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>> {

        return sudLifePolicyRepository.sudGetReceiptDetails(data)
    }

    suspend fun invokeSudGetPMJJBYCoiBase(data: SudPMJJBYCoiBaseModel): LiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>> {

        return sudLifePolicyRepository.sudGetPMJJBYCoiBase(data)
    }

    suspend fun invokeSudGetKypTemplate(data: SudKypTemplateModel): LiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>> {

        return sudLifePolicyRepository.sudGetKypTemplate(data)
    }

    suspend fun invokeSudKypPdf(data: SudKypPdfModel): LiveData<Resource<SudKypPdfModel.SudKypPdfResponse>> {

        return sudLifePolicyRepository.sudKypPdf(data)
    }

    suspend fun invokeSudGetPayPremium(data: SudPayPremiumModel): LiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>> {

        return sudLifePolicyRepository.sudGetPayPremium(data)
    }

    suspend fun invokeSudGetCreditLifeCoi(data: SudCreditLifeCOIModel): LiveData<Resource<SudCreditLifeCOIModel.SudCreditLifeCOIResponse>> {

        return sudLifePolicyRepository.sudGetCreditLifeCoi(data)
    }

    suspend fun invokeSudGetRenewalPremium(data: SudRenewalPremiumModel): LiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>> {

        return sudLifePolicyRepository.sudGetRenewalPremium(data)
    }

    suspend fun invokeSudGetRenewalPremiumReceipt(data: SudRenewalPremiumReceiptModel): LiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>> {

        return sudLifePolicyRepository.sudGetRenewalPremiumReceipt(data)
    }

    suspend fun invokeGenerateOTP(
        isForceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return userRepository.getGenerateOTPResponse(isForceRefresh, data)
    }

    suspend fun invokeValidateOTP(
        isForceRefresh: Boolean,
        data: GenerateOtpModel
    ): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return userRepository.getValidateOTPResponse(isForceRefresh, data)
    }

    /*    suspend fun invokeUpdateOtpAuthentication(isOtpAuthenticated:Boolean) {
            userRepository.updateOtpAuthentication(isOtpAuthenticated)
        }*/

}