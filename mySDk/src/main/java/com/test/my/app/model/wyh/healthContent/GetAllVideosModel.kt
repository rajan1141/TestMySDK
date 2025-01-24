package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllVideosModel {

    data class GetAllVideosRequest(
        @Expose
        @SerializedName("startRow")
        val startRow: Int? = 0,
        @Expose
        @SerializedName("pageSize")
        val pageSize: Int? = 0,
        @SerializedName("videoTags")
        @Expose
        val videoTags: String? = "",
        @SerializedName("searchKey")
        @Expose
        val searchKey: String? = ""
    )

    data class GetAllVideosResponse(
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
/*        @SerializedName("rewards")
        @Expose
        val rewards: Rewards = Rewards()*/
    )

    data class Data(
        @SerializedName("totalRowCount")
        @Expose
        val totalRowCount: Int? = 0,
        @SerializedName("data")
        @Expose
        val data : List<Video>? = listOf()
    )

    data class Video(
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("videoName")
        @Expose
        val videoName: String? = "",
        @SerializedName("videoTag")
        @Expose
        val videoTag: String? = "",
        @SerializedName("videoUrl")
        @Expose
        val videoUrl: String? = "",
        @SerializedName("productUrl")
        @Expose
        val productUrl: String? = "",
        @SerializedName("thumbnailImage")
        @Expose
        val thumbnailImage: String? = "",
        @SerializedName("description")
        @Expose
        val description: String? = "",
        @SerializedName("isActive")
        @Expose
        val isActive: Boolean? = false,
        @SerializedName("searchKey")
        @Expose
        val searchKey: String? = "",
        @SerializedName("startDate")
        @Expose
        val startDate: String? = "",
        @SerializedName("endDate")
        @Expose
        val endDate: String? = "",
        @SerializedName("createdBy")
        @Expose
        val createdBy: String? = "",
        @SerializedName("createdOn")
        @Expose
        val createdOn: String? = "",
        @SerializedName("modifiedBy")
        @Expose
        val modifiedBy: String? = "",
        @SerializedName("modifiedOn")
        @Expose
        val modifiedOn: String? = "",
        @SerializedName("corpoCode")
        @Expose
        val corpoCode: String? = ""
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