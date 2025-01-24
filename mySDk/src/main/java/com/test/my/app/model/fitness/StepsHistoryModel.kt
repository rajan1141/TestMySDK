package com.test.my.app.model.fitness

import com.test.my.app.model.BaseRequest
import com.test.my.app.model.entity.FitnessEntity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StepsHistoryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken.toString())) {

    data class JSONDataRequest(
        @SerializedName("Message")
        val message: String,
        @SerializedName("SearchCriteria")
        val searchCriteria: SearchCriteria
    )

    data class SearchCriteria(
        @SerializedName("PersonID")
        val personID: String,
        @SerializedName("FromDate")
        val fromDate: String,
        @SerializedName("ToDate")
        val toDate: String
    )

    data class Response(
        @SerializedName("StepGoalHistory")
        var stepGoalHistory: List<FitnessEntity.StepGoalHistory> = listOf()
    )

    /*data class StepGoalHistory(
        @SerializedName("Calories")
        val calories: Int,
        @SerializedName("Distance")
        val distance: Double,
        @SerializedName("GoalID")
        val goalID: Int,
        @SerializedName("GoalPercentile")
        val goalPercentile: Double,
        @SerializedName("RecordDate")
        val recordDate: String,
        @SerializedName("StepID")
        val stepID: Int,
        @SerializedName("StepsCount")
        val stepsCount: Int,
        @SerializedName("TotalGoal")
        val totalGoal: Int
    )*/
}