package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RemoveProfileImageModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: Int = 0
    )

    data class RemoveProfileImageResponse(
        @SerializedName("IsProcessed")
        val isProcessed: String = ""
    )

}