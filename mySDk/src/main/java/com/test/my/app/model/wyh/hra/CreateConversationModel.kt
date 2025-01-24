package com.test.my.app.model.wyh.hra

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateConversationModel {

    data class CreateConversationRequest(
        @SerializedName("integrationId")
        @Expose
        val integrationId: String? = ""
    )

    data class CreateConversationResponse(
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
        val data: Data = Data(),

        @SerializedName("appversion")
        @Expose
        val appversion: String? = "",
        @SerializedName("isCloud")
        @Expose
        val isCloud: Boolean? = false,
        @SerializedName("rewards")
        @Expose
        val rewards: Rewards = Rewards()
    )

    data class Data(
        @SerializedName("stressLevel")
        @Expose
        val stressLevel: String? = ""
    )

    data class Rewards(
        @SerializedName("bonusRewards")
        @Expose
        val bonusRewards: Any? = Any(),
        @SerializedName("reward")
        @Expose
        val reward: Any? = Any()
    )

}