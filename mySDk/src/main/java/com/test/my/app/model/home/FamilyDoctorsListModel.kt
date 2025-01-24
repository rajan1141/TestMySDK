package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FamilyDoctorsListModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        @Expose
        private val accountID: String = ""
    )

    data class FamilyDoctorsResponse(
        @SerializedName("ListFamilyDoctors")
        val listFamilyDoctors: List<FamilyDoctor> = listOf()
    )

}