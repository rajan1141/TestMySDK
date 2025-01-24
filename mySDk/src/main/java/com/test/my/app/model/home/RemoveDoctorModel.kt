package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RemoveDoctorModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ID")
        val id: List<Int>
    )


    data class RemoveDoctorResponse(
        @SerializedName("IsProcessed")
        val isProcessed: String = ""
    )

}