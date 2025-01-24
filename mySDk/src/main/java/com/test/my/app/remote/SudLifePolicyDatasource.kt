package com.test.my.app.remote

import com.test.my.app.di.DIModule.DEFAULT_NEW
import com.test.my.app.di.DIModule.ENCRYPTED
import com.test.my.app.di.DIModule.SUD
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
import javax.inject.Inject
import javax.inject.Named

class SudLifePolicyDatasource @Inject constructor(
    @Named(DEFAULT_NEW) private val defaultService: ApiService,
    @Named(ENCRYPTED) private val encryptedUserService: ApiService,
    @Named(SUD)  private val sudLifePolicyService: ApiService
) {

    suspend fun policyProductsResponse(data: PolicyProductsModel) =
        encryptedUserService.policyProductsApi(data)

    suspend fun sudPolicyByMobileNumberResponse(data: SudPolicyByMobileNumberModel) =
        defaultService.getSudPolicyByMobileNumber(data)

    suspend fun sudPolicyDetailsByPolicyNumber(data: SudPolicyDetailsByPolicyNumberModel) =
        defaultService.getPolicyDetailsByPolicyNumber(data)

    suspend fun sudGetFundDetail(data: SudFundDetailsModel) = defaultService.getSudFundDetail(data)

    suspend fun sudGetReceiptDetails(data: SudReceiptDetailsModel) =
        defaultService.getReceiptDetails(data)

    suspend fun sudGetPMJJBYCoiBase(data: SudPMJJBYCoiBaseModel) =
        defaultService.getSudPMJJBYCoiBase(data)

    suspend fun sudGetKypTemplate(data: SudKypTemplateModel) = defaultService.getKypTemplate(data)

    suspend fun sudKypPdf(data: SudKypPdfModel) = defaultService.getSudKypPdf(data)

    suspend fun sudGetPayPremium(data: SudPayPremiumModel) = defaultService.getPayPremium(data)

    suspend fun sudGetCreditLifeCoi(data: SudCreditLifeCOIModel) = defaultService.getCreditLifeCoi(data)

    suspend fun sudKYP(data: SudKYPModel) = defaultService.getSudKYP(data)

    suspend fun sudGroupCoiApi(data: SudGroupCOIModel) = encryptedUserService.groupCoiApi(data)

    suspend fun sudGetRenewalPremium(data: SudRenewalPremiumModel) =
        defaultService.getRenewalPremium(data)

    suspend fun sudGetRenewalPremiumReceipt(data: SudRenewalPremiumReceiptModel) =
        defaultService.getRenewalPremiumReceipt(data)

}