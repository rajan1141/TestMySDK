package com.test.my.app.model.wyh

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WyhAuthorizationModel {

    data class WyhAuthorizationResponse(
        @Expose
        @SerializedName("success")
        val success: Boolean? = false,
        @Expose
        @SerializedName("msg")
        val msg: String? = "",
        @Expose
        @SerializedName("isUnderMaintenance")
        val isUnderMaintenance: Boolean? = false,
        @Expose
        @SerializedName("appversion")
        val appversion: String? = "",
        @Expose
        @SerializedName("isCloud")
        val isCloud: Boolean? = false,
        @Expose
        @SerializedName("data")
        val data: String? = "",
/*        @Expose
        @SerializedName("rewards")
        val rewards: Rewards = Rewards()*/
    )

    data class Rewards(
        @Expose
        @SerializedName("bonusRewards")
        val bonusRewards: Any? = Any(),
        @Expose
        @SerializedName("reward")
        val reward: Any? = Any()
    )

}