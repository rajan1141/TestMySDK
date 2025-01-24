package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FitrofySdpModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @Expose
        @SerializedName("PersonID")
        var personID: Int = 0
    )

    data class FitrofySdpResponse(
        @Expose
        @SerializedName("GetFitrofyURL")
        val getFitrofyURL: GetFitrofyURL = GetFitrofyURL()
    )

    data class GetFitrofyURL(
        @Expose
        @SerializedName("url")
        val url: String? = ""
    )

}