package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllAudiosModel {

    data class GetAllAudiosRequest(
        @Expose
        @SerializedName("startRow")
        val startRow: Int? = 0,
        @Expose
        @SerializedName("pageSize")
        val pageSize: Int? = 0,
        @SerializedName("audioTags")
        @Expose
        val audioTags: String? = "",
        @SerializedName("searchKey")
        @Expose
        val searchKey: String? = ""
    )

    data class GetAllAudiosResponse(
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
        val data : List<Audio>? = listOf()
    )

    data class Audio(
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("audioName")
        @Expose
        val audioName: String? = "",
        @SerializedName("audioUrl")
        @Expose
        val audioUrl: String? = "",
        @SerializedName("thumbnailImage")
        @Expose
        val thumbnailImage: String? = "",
        @SerializedName("audioTag")
        @Expose
        val audioTag: Any? = Any(),
        @SerializedName("description")
        @Expose
        val description: String? = "",
        @SerializedName("createdBy")
        @Expose
        val createdBy: String? = "",
        @SerializedName("createdOn")
        @Expose
        val createdOn: String? = "",
        @SerializedName("isActive")
        @Expose
        val isActive: Boolean? = false,
        @SerializedName("searchKeys")
        @Expose
        val searchKeys: Any? = Any()
    )

}