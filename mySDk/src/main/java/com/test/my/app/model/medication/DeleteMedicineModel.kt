package com.test.my.app.model.medication

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteMedicineModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("MedicationID")
        val medicationID: String = "",
        @SerializedName("Message")
        val message: String = "Deleting medicine...."
    )

    data class DeleteMedicineResponse(
        @SerializedName("IsProcessed")
        var isProcessed: Boolean = false
    )

}