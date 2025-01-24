package com.test.my.app.model.parameter


import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BMIHistoryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        val personID: String = "0",
        @SerializedName("LastSyncDate")
        @Expose
        var lastSyncDate: String = "01-JAN-1901",
        @SerializedName("NumberOfReadings")
        @Expose
        var numberOfReadings: String = "5"
    )

    data class Response(
        @SerializedName("BMIHistory")
        val bMIHistory: List<BMIHistory> = listOf()
    )

    data class BMIHistory(
        @SerializedName("Height")
        val height: Double = 0.0,
        @SerializedName("Weight")
        val weight: Double = 0.0,
        @SerializedName("Value")
        val value: String = "",
        @SerializedName("Observation")
        val observation: String = "",
        @SerializedName("RecordDate")
        var recordDate: String = "",
        var recordDateMillisec: String? = "",
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("Percentile")
        val percentile: String = "",
        @SerializedName("OwnerCode")
        val ownerCode: Any = Any(),
        @SerializedName("Comments")
        val comments: Any = Any(),
        @SerializedName("MaxWellnessScore")
        val maxWellnessScore: Any = Any(),
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = ""
    )

}

