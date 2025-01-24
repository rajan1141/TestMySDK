package com.test.my.app.model.shr

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OCRSaveModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("LabRecords")
        val LabRecords: List<LabRecord> = listOf()
    )

    data class LabRecord(
        @SerializedName("ParameterCode")
        var ParameterCode: String,
        @SerializedName("PersonID")
        var PersonID: String,
        @SerializedName("RecordDate")
        var RecordDate: String,
        @SerializedName("TextValue")
        var TextValue: String,
        @SerializedName("Unit")
        var Unit: String,
        @SerializedName("Value")
        var Value: String
    )

    data class OCRSaveResponse(
        @SerializedName("IsExist")
        val isSaved: Boolean = false
    )
}