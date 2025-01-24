package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateLanguageProfileModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Details")
        val details: Details = Details()
    )

    data class Details(
        @SerializedName("PersonID")
        val personId: String = "",
        @SerializedName("LanguageCode")
        val languageCode: String = ""
    )

    data class UpdateLanguageProfileResponse(
        @SerializedName("IsUpdated")
        val isUpdated: String = "True"
    )

}