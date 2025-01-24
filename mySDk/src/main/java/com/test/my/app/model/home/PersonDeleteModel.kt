package com.test.my.app.model.home

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PersonDeleteModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Reason")
        var reason: String = "",
        @SerializedName("RequestedDateTime")
        var requestedDateTime: String = "",
        @SerializedName("AccountID")
        var accountID: Int = 0
    )

    data class PersonDeleteResponse(
        @SerializedName("AccountID")
        var accountID: String = ""
    )

}
