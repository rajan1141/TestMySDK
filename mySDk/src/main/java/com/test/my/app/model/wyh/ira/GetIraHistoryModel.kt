package com.test.my.app.model.wyh.ira

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetIraHistoryModel {

    data class GetIRAHistoryRequest(
        @SerializedName("startRow")
        @Expose
        val startRow: Int = 0,
        @SerializedName("pageSize")
        @Expose
        val pageSize: Int = 0,
        @SerializedName("sortField")
        @Expose
        val sortField: String? = "createdAt",
        @SerializedName("asc")
        @Expose
        val asc: Boolean? = false
    )

    data class GetIRAHistoryResponse(
        @SerializedName("success")
        @Expose
        val success: Boolean? = false,
        @SerializedName("msg")
        @Expose
        val msg: String? = "",
        @SerializedName("isUnderMaintenance")
        @Expose
        val isUnderMaintenance: Boolean? = false,

        @SerializedName("data")
        @Expose
        val data: Data? = Data(),

        @SerializedName("appversion")
        @Expose
        val appversion: String? = "",
        @SerializedName("isCloud")
        @Expose
        val isCloud: Boolean? = false,
    )

    data class Data(
        @SerializedName("totalRowCount")
        @Expose
        val totalRowCount: Int? = 0,
        @SerializedName("data")
        @Expose
        val data: List<DataResp>? = listOf()
    )

    data class DataResp(
        @SerializedName("addictedToTV")
        @Expose
        val addictedToTV: Any? = Any(),
        @SerializedName("appreciation")
        @Expose
        val appreciation: Any? = Any(),
        @SerializedName("communting")
        @Expose
        val communting: Any? = Any(),
        @SerializedName("contactManyPeople")
        @Expose
        val contactManyPeople: String? = "",
        @SerializedName("content")
        @Expose
        val content: String? = "",
        @SerializedName("conversationID")
        @Expose
        val conversationID: String? = "",
        @SerializedName("crackedLips")
        @Expose
        val crackedLips: String? = "",
        @SerializedName("createdAt")
        @Expose
        val createdAt: String? = "",
        @SerializedName("dietBalanced")
        @Expose
        val dietBalanced: String? = "",
        @SerializedName("exercise")
        @Expose
        val exercise: String? = "",
        @SerializedName("familyTime")
        @Expose
        val familyTime: Any? = Any(),
        @SerializedName("feeltired")
        @Expose
        val feeltired: String? = "",
        @SerializedName("healthCheckUp")
        @Expose
        val healthCheckUp: Any? = Any(),
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("insertedAt")
        @Expose
        val insertedAt: String? = "",
        @SerializedName("insomnia")
        @Expose
        val insomnia: String? = "",
        @SerializedName("loseTemper")
        @Expose
        val loseTemper: String? = "",
        @SerializedName("medication")
        @Expose
        val medication: String? = "",
        @SerializedName("meditate")
        @Expose
        val meditate: Any? = Any(),
        @SerializedName("oftenStressful")
        @Expose
        val oftenStressful: String? = "",
        @SerializedName("questionsScore")
        @Expose
        val questionsScore: Int? = 0,
        @SerializedName("riskLevel")
        @Expose
        val riskLevel: String? = "",
        @SerializedName("score")
        @Expose
        val score: Int? = 0,
        @SerializedName("screenTime")
        @Expose
        val screenTime: Any? = Any(),
        @SerializedName("sleepHours")
        @Expose
        val sleepHours: Any? = Any(),
        @SerializedName("smoke")
        @Expose
        val smoke: String? = "",
        @SerializedName("soundSleeper")
        @Expose
        val soundSleeper: Any? = Any(),
        @SerializedName("spendtimeoutdoors")
        @Expose
        val spendtimeoutdoors: String? = "",
        @SerializedName("status")
        @Expose
        val status: String? = "",
        @SerializedName("stress")
        @Expose
        val stress: Any? = Any(),
        @SerializedName("sufferColdFlu")
        @Expose
        val sufferColdFlu: String? = "",
        @SerializedName("sufferFrom")
        @Expose
        val sufferFrom: Any? = Any(),
        @SerializedName("sufferIllnesses")
        @Expose
        val sufferIllnesses: String? = "",
        @SerializedName("uuid")
        @Expose
        val uuid: String? = ""
    )

}