package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllBlogsModel {

    data class GetAllBlogsRequest(
        @Expose
        @SerializedName("startRow")
        val startRow: Int? = 0,
        @Expose
        @SerializedName("pageSize")
        val pageSize: Int? = 0,
        @SerializedName("articleCode")
        @Expose
        val articleCode: String? = "",
        @SerializedName("tags")
        @Expose
        val tags: String? = "",
        @SerializedName("searchKey")
        @Expose
        val searchKey: String? = ""
    )

    data class GetAllBlogsResponse(
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
        val data : List<Blog>? = listOf()
    )

    data class Blog(
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("articleName")
        @Expose
        val articleName: String? = "",
        @SerializedName("articlePath")
        @Expose
        val articlePath: String? = "",
        @SerializedName("imgPath")
        @Expose
        val imgPath: String? = "",
        @SerializedName("htmlContent")
        @Expose
        val htmlContent: String? = "",


        @SerializedName("active")
        @Expose
        val active: Boolean? = false,
        @SerializedName("articleCode")
        @Expose
        val articleCode: String? = "",
        @SerializedName("blogBitly")
        @Expose
        val blogBitly: String? = "",
        @SerializedName("readcount")
        @Expose
        val readcount: Int? = 0,
        @SerializedName("searchKeys")
        @Expose
        val searchKeys: String? = "",
        @SerializedName("tags")
        @Expose
        val tags: String? = "",
        @SerializedName("createdBy")
        @Expose
        val createdBy: String? = "",
        @SerializedName("createdOn")
        @Expose
        val createdOn: String? = "",
        @SerializedName("updatedBy")
        @Expose
        val updatedBy: String? = "",
        @SerializedName("updatedOn")
        @Expose
        val updatedOn: String? = "",
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