package com.test.my.app.model.wyh.faceScan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetFaceScanVitalsModel {

    data class GetFaceScanVitalsResponse(
        @SerializedName("success")
        @Expose
        val success: Boolean? = false,
        @SerializedName("appversion")
        @Expose
        val appversion: String? = "",
        @SerializedName("data")
        @Expose
        val data: Data? = Data(),
        @SerializedName("isCloud")
        @Expose
        val isCloud: Boolean? = false,
        @SerializedName("isUnderMaintenance")
        @Expose
        val isUnderMaintenance: Boolean? = false,
        @SerializedName("msg")
        @Expose
        val msg: String? = "",
/*        @SerializedName("rewards")
        @Expose
        val rewards: Rewards = Rewards()*/
    )

    data class Data(
        @SerializedName("Id")
        @Expose
        val id: Int? = 0,
        @SerializedName("userId")
        @Expose
        val userId: String? = "",
        @SerializedName("heartRate")
        @Expose
        val heartRate: Int? = 0,
        @SerializedName("respiratoryRate")
        @Expose
        val respiratoryRate: Int? = 0,
        @SerializedName("oxygen")
        @Expose
        val oxygen: Int? = 0,
        @SerializedName("systolic")
        @Expose
        val systolic: Int? = 0,
        @SerializedName("diastolic")
        @Expose
        val diastolic: Int? = 0,
        @SerializedName("stressStatus")
        @Expose
        val stressStatus: String? = "",
        @SerializedName("bloodPressureStatus")
        @Expose
        val bloodPressureStatus: String? = "",
        @SerializedName("sdnn")
        @Expose
        val sdnn: Int? = 0,
        @SerializedName("height")
        @Expose
        val height: Int? = 0,
        @SerializedName("weight")
        @Expose
        val weight: Int? = 0,
        @SerializedName("age")
        @Expose
        val age: Int? = 0,
        @SerializedName("gender")
        @Expose
        val gender: String? = "",

        @SerializedName("CreatedOn")
        @Expose
        val createdOn: String? = "",
        @SerializedName("CreatedBy")
        @Expose
        val createdBy: String? = "",
        @SerializedName("IsActive")
        @Expose
        val isActive: Boolean? = false,

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