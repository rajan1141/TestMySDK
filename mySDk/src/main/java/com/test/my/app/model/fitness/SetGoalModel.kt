package com.test.my.app.model.fitness

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SetGoalModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken.toString())) {

    data class JSONDataRequest(
        @SerializedName("Message")
        val message: String,
        @SerializedName("StepsGoals")
        val stepsGoals: StepsGoalsReq
    )

    data class StepsGoalsReq(
        @SerializedName("Date")
        val date: String,
        @SerializedName("Goal")
        val goal: Int,
        @SerializedName("PersonID")
        val personID: String,
        @SerializedName("Type")
        val type: String
    )

    data class Response(
        @SerializedName("StepsGoals")
        val stepsGoals: StepsGoals = StepsGoals()
    )

    data class StepsGoals(
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("Date")
        val date: String = "",
        @SerializedName("Type")
        val type: String = "",
        @SerializedName("Goal")
        val goal: Int = 0,
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("ModifiedBy")
        val modifiedBy: String = "",
        @SerializedName("ModifiedDate")
        val modifiedDate: String = ""
    )

}