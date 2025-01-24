package com.test.my.app.model.security


import com.test.my.app.model.BaseRequest
//import com.test.my.app.model.tempconst.Configuration
import com.test.my.app.common.constants.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class GenerateOtpModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(
    Header(
        authTicket = ""
    )
) {

    data class JSONDataRequest(
        @SerializedName("UPN")
        @Expose
        val upn: UPN,
        @SerializedName("OTP")
        @Expose
        var otp: String = "",
        @SerializedName("from")
        @Expose
        var from: String = "",
        @SerializedName("Message")
        @Expose
        var message: String = ""

    )

    data class GenerateOTPResponse(
        @SerializedName("Status")
        @Expose
        var status: String? = "",
        @SerializedName("VALIDITY")
        @Expose
        var validity: String? = ""

    )

    data class UPN(
        @SerializedName("LoginName")
        @Expose
        private val loginName: String = "",
        @SerializedName("EmailAddress")
        @Expose
        private val emailAddress: String = "",
        @SerializedName("PrimaryPhone")
        @Expose
        private val primaryPhone: String = "",
        @SerializedName("ApplicationCode")
        @Expose
        private val applicationCode: String = Configuration.ApplicationCode
    )
}