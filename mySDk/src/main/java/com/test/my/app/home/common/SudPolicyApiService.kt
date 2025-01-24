package com.test.my.app.home.common

import com.test.my.app.common.constants.Constants
import com.test.my.app.model.sudLifePolicy.SudKYPModel
import retrofit2.Call
import retrofit2.http.*

interface SudPolicyApiService {
    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    fun getSudKyp(@Body request: SudKYPModel): Call<SudKYPModel.SudKYPResponse>

/*    @Headers("AppName:CreditLifeKYP",
        "APIKey:c79e7d08-ee03-4185-9499-aec3eabe3f2d",
        "SourceSystem:YouMatter_P101")
    @POST("WAApi/CreditLifeKYPwrapper")
    fun getCreditLife(@Body request: SudCreditLifeCOIModel.JSONDataRequest): Call<SudCreditLifeCOIModel.CreditLifeCOIResponse>*/
}