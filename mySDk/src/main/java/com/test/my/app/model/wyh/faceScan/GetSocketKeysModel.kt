package com.test.my.app.model.wyh.faceScan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetSocketKeysModel {

    data class GetSocketKeysResponse(
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
        val data: List<Data> = listOf(),
        @SerializedName("appversion")
        @Expose
        val appversion: String? = "",
        @SerializedName("isCloud")
        @Expose
        val isCloud: Boolean? = false,
/*        @SerializedName("rewards")
        @Expose
        val rewards: Rewards = Rewards()*/
    )

    data class Data(
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("internalId")
        @Expose
        val internalId: String? = "",
        @SerializedName("companyId")
        @Expose
        val companyId: Int? = 0,
        @SerializedName("value")
        @Expose
        val value: String? = "",
        @SerializedName("accessLimit")
        @Expose
        val accessLimit: Any? = Any(),
        @SerializedName("activationDateTime")
        @Expose
        val activationDateTime: String? = "",
        @SerializedName("creationDateTime")
        @Expose
        val creationDateTime: String? = "",
        @SerializedName("expirationDateTime")
        @Expose
        val expirationDateTime: String? = ""
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

