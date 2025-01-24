package com.test.my.app.model.wyh.ira

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetIraAnswersScoreModel {

    data class GetIraAnswersScoreRequest(
        @SerializedName("integrationid")
        @Expose
        val integrationid : String? = "F6D3838A-D357-4AA5-900F-CF53C77A241C",
        @SerializedName("conversationId")
        @Expose
        val conversationId : String? = "",
    )

    data class GetIraAnswersScoreResponse(
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
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("conversationId")
        @Expose
        val conversationId: String? = "",
        @SerializedName("uuid")
        @Expose
        val uuid: String? = "",
        @SerializedName("score")
        @Expose
        val score: Int? = 0
    )

}