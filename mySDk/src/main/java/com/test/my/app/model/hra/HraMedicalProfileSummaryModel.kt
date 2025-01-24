package com.test.my.app.model.hra

import com.test.my.app.model.BaseRequest
import com.test.my.app.model.entity.HRASummary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraMedicalProfileSummaryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val PersonID: String = "",
        @SerializedName("LastSyncDate")
        @Expose
        private val LastSyncDate: String = "01-JAN-1901"
    )

    data class MedicalProfileSummaryResponse(
        @SerializedName("MedicalProfileSummary")
        @Expose
        var MedicalProfileSummary: HRASummary? = null
    )

}