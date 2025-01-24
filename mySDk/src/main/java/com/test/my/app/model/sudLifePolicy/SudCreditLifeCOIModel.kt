package com.test.my.app.model.sudLifePolicy

import com.test.my.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudCreditLifeCOIModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Contract_No")
        @Expose
        var contractNo: String = ""
    )

    data class SudCreditLifeCOIResponse(
        @Expose
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("BASE64")
        @Expose
        val pdfBASE64: String? = ""
/*        @SerializedName("PDF_LINK")
        @Expose
        val pdfLink: String? = ""*/
    )

}

/*
class SudCreditLifeCOIModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Contract_No")
        @Expose
        var contractNo: String = ""
    )

    data class SudCreditLifeCOIResponse(
        @Expose
        @SerializedName("CreditLifeCOIResponse")
        val creditLifeCOIResponse: CreditLifeCOIResponse = CreditLifeCOIResponse()
    )

    data class CreditLifeCOIResponse(
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("BASE64")
        @Expose
        val pdfBASE64: String? = ""
    )

}*/
