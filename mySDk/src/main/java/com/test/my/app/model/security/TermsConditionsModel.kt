package com.test.my.app.model.security

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TermsConditionsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(
    Header(
        authTicket = ""
    )
) {

    data class JSONDataRequest(
        @SerializedName("ApplicationCode")
        @Expose
        var applicationCode: String? = "",
        @SerializedName("PartnerCode")
        @Expose
        var partnerCode: String? = ""
    )

    data class TermsConditionsResponse(
        @SerializedName("TermsAndConditions")
        @Expose
        var termsConditions: TermsConditions? = null
    )

    data class TermsConditions(
        @SerializedName("Description")
        @Expose
        var description: String? = ""
    )

}