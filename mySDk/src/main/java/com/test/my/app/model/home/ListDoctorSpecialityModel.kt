package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListDoctorSpecialityModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Speciality")
        @Expose
        private val speciality: String = ""
    )

    data class ListDoctorSpecialityResponse(
        @SerializedName("Speciality")
        val speciality: List<Speciality> = listOf()
    )

}