package com.test.my.app.model.wyh.faceScan

import com.test.my.app.common.utils.DateHelper
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddFaceScanVitalsModel {

    data class AddFaceScanVitalsRequest(
        @SerializedName("heartRate")
        @Expose
        val heartRate: Int? = 0,
        @SerializedName("respiratoryRate")
        @Expose
        val respiratoryRate: Int? = 0,
        @SerializedName("oxygen")
        @Expose
        val oxygen: Int? = 0,
        @SerializedName("stressLevel")
        @Expose
        val stressLevel: String? = "",
        @SerializedName("bloodPressureStatus")
        @Expose
        val bloodPressureStatus: String? = "",
        @SerializedName("systolic")
        @Expose
        val systolic: Int? = 0,
        @SerializedName("diastolic")
        @Expose
        val diastolic: Int? = 0,
        @SerializedName("hrv")
        @Expose
        val hrv: Int? = 0,

        @SerializedName("gender")
        @Expose
        val gender: String? = "",
        @SerializedName("age")
        @Expose
        val age: Int? = 0,
        @SerializedName("height")
        @Expose
        val height: Int? = 0,
        @SerializedName("weight")
        @Expose
        val weight: Int? = 0,

        @SerializedName("isAnalysis")
        @Expose
        val isAnalysis: Boolean? = true,
        @SerializedName("createdBy")
        @Expose
        val createdBy: String? = "Self",
        @SerializedName("modifiedBy")
        @Expose
        val modifiedBy: String? = "Self",

        @SerializedName("createdOn")
        @Expose
        val createdOn: String? = DateHelper.currentUTCDatetimeInMillisecAsString,
        @SerializedName("modifiedOn")
        @Expose
        val modifiedOn: String? = DateHelper.currentUTCDatetimeInMillisecAsString
    )

    data class AddFaceScanVitalsResponse(
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
/*        @SerializedName("rewards")
        @Expose
        val rewards: Rewards = Rewards()*/
    )

    data class Data(
        @SerializedName("avgBMI")
        @Expose
        val avgBMI: Double? = 0.0,
        @SerializedName("bmi")
        @Expose
        val bmi: Double? = 0.0,
        @SerializedName("bmiRemark")
        @Expose
        val bmiRemark: String? = "",
        @SerializedName("bodyFat")
        @Expose
        val bodyFat: Double? = 0.0,
        @SerializedName("bpValue")
        @Expose
        val bpValue: Int? = 0,
        @SerializedName("finalScore")
        @Expose
        val finalScore: Int? = 0,
        @SerializedName("gainWeight")
        @Expose
        val gainWeight: Int? = 0,
        @SerializedName("heartRateValue")
        @Expose
        val heartRateValue: Int? = 0,
        @SerializedName("hrvValue")
        @Expose
        val hrvValue: Int? = 0,
        @SerializedName("idealBMI")
        @Expose
        val idealBMI: Double? = 0.0,
        @SerializedName("looseWeight")
        @Expose
        val looseWeight: Double? = 0.0,
        @SerializedName("rrValue")
        @Expose
        val rrValue: Int? = 0,
        @SerializedName("scoreRemark")
        @Expose
        val scoreRemark: String? = "",
        @SerializedName("spO2")
        @Expose
        val spO2: Int? = 0,
        @SerializedName("stressValue")
        @Expose
        val stressValue: Int? = 0,
        @SerializedName("weightvalue")
        @Expose
        val weightvalue: Int? = 0,
        @SerializedName("createdOn")
        @Expose
        val createdOn: String? = DateHelper.currentUTCDatetimeInMillisecAsString
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