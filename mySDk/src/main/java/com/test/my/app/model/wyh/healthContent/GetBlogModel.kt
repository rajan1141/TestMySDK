package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetBlogModel {

    data class GetBlogRequest(
        @SerializedName("articleCode")
        @Expose
        val articleCode : String? = ""
    )

    data class GetBlogResponse(
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
        val data: Article? = Article(),

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

    data class Article(
        @SerializedName("articleID")
        @Expose
        val articleID: Int? = 0,
        @SerializedName("articleCode")
        @Expose
        val articleCode: String? = "",
        @SerializedName("articleName")
        @Expose
        val articleName: String? = "",
        @SerializedName("tags")
        @Expose
        val tags: String? = "",
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
        val active: Int? = 0,
        @SerializedName("isBookMarked")
        @Expose
        val isBookMarked: Boolean? = false
    )

}