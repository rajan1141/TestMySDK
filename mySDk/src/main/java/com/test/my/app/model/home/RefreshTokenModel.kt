package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RefreshTokenModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        val accountID: String = "",
        @SerializedName("PersonID")
        val personID: String = ""
    )

    data class RefreshTokenResponse(
        @SerializedName("Response")
        var response: Response = Response()
    )

    data class Response(
        @SerializedName("Data")
        var `data`: Data = Data(),
        @SerializedName("StatusCode")
        var statusCode: Int = 0
    )

    data class Data(
        @SerializedName("Context")
        var context: String = ""
    )
}