package com.test.my.app.model.nimeya

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRetiroMeterHistoryModel(
    @Expose
    @SerializedName("JSONData")
    private val jsonData: String,
    @Transient
    private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @Expose
        @SerializedName("PersonID")
        var personID: Int = 0,
        @Expose
        @SerializedName("AccountID")
        var accountID: Int = 0
    )

    data class RetiroMeterHistoryResponse(
        @Expose
        @SerializedName("GetRetiroMeterHistory")
        val data: GetRetiroMeterHistory = GetRetiroMeterHistory()
    )

    data class GetRetiroMeterHistory(
        @Expose
        @SerializedName("PersonID")
        var personID: Int = 0,
        @Expose
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @Expose
        @SerializedName("DateTime")
        val dateTime: String? = "",
        @Expose
        @SerializedName("Score")
        val score: Int? = 0,
        @Expose
        @SerializedName("ScoreText")
        val scoreText: String? = "",
        @Expose
        @SerializedName("Message")
        val message: String? = "",

        @Expose
        @SerializedName("YourDesiredRetirementIncome")
        val yourDesireRetirementIncome: String? = "",
        @Expose
        @SerializedName("ProjectedMonthlyRetirementIncomePerMonth")
        val projectedMonthlyRetirementIncomePerMonth: String? = "",
        @Expose
        @SerializedName("RequiredMonthlyRetirementSavings")
        val requiredMonthlyRetirementSavings: String? = "",
        @Expose
        @SerializedName("RequiredPPF")
        val requiredPpf: String? = "",
        @Expose
        @SerializedName("RequiredEPF")
        val requiredEpf: String? = "",
        @Expose
        @SerializedName("RequiredNPS")
        val requiredNps: String? = "",
        @Expose
        @SerializedName("RequiredEquityMutualFunds")
        val requiredEquityMutualFunds: String? = ""
    )

}