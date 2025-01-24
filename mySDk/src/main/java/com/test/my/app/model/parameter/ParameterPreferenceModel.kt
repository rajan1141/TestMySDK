package com.test.my.app.model.parameter

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ParameterPreferenceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {


    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        val personId: String = "",
        @SerializedName("from")
        @Expose
        val from: String = "66",
        @SerializedName("Message")
        @Expose
        var message: String = ""
    )

    data class Response(
        @SerializedName("Records")
        val records: List<Record> = listOf()
    )

    data class Record(
        @SerializedName("ID")
        val iD: String = "",
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("ParameterName")
        val parameterName: String = "",
        @SerializedName("ProfileCode")
        val profileCode: String = "",
        @SerializedName("ProfileName")
        val profileName: String = ""
    )
}