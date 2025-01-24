package com.test.my.app.model.shr

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OCRUnitExistModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("Unit")
        val unit: String = ""
    )

    data class OCRUnitExistResponse(
        @SerializedName("IsExist")
        val isExist: Boolean = true
    )
}