package com.test.my.app.model.sudLifePolicy

import com.test.my.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SudPolicyByMobileNumberModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class SudPolicyByMobileNumberResponse(
        @SerializedName("Result")
        @Expose
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("records")
        @Expose
        val records: List<Record> = listOf(),
        @SerializedName("status")
        @Expose
        val status: String? = ""
    )

    data class Record(
        @SerializedName("Client_Role")
        @Expose
        val clientRole: String? = "",
        @SerializedName("Customer_Code")
        @Expose
        val customerCode: String? = "",
        @SerializedName("Policy_Number")
        @Expose
        val policyNumber: String? = "",

        @SerializedName("Product_Code")
        @Expose
        val productCode: String? = "",
        @SerializedName("PolicyType")
        @Expose
        val policyType: String? = "",
        @SerializedName("Language")
        @Expose
        val language: String? = "",
        @SerializedName("Plan_Name")
        @Expose
        val planName: String? = "",
        var isSelected: Boolean = false
    )
}