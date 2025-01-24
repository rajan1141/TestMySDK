package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAllItemsModel {

    data class GetAllItemsRequest(
        @SerializedName("itemType")
        @Expose
        val itemType : String? = "",
        @SerializedName("tagName")
        @Expose
        val tagName : String? = "",
        @SerializedName("userId")
        @Expose
        val userId : String? = "",
        @Expose
        @SerializedName("pagenumber")
        val pagenumber : Int? = 0,
        @Expose
        @SerializedName("communityId")
        val communityId : Int? = 0
    )

    data class GetAllItemsResponse(
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
        val quickReadData: QuickReadData? = QuickReadData(),

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

    data class QuickReadData(
        @SerializedName("trendingBlogs")
        @Expose
        val trendingBlogs : List<Article>? = listOf(),
        @SerializedName("blogs")
        @Expose
        val blogs : List<Article>? = listOf(),
        @SerializedName("mostRead")
        @Expose
        val mostRead : List<Article>? = listOf(),
        @SerializedName("tribeBlogs")
        @Expose
        val tribeBlogs : List<Article>? = listOf(),
        @SerializedName("bookMarkBlogs")
        @Expose
        val bookMarkBlogs : List<Article>? = listOf(),
        @SerializedName("tribeVideos")
        @Expose
        val tribeVideos : List<Article>? = listOf(),
/*        @SerializedName("allBlogs")
        @Expose
        val allBlogs : List<Article>? = listOf(),
        @SerializedName("allVideo")
        @Expose
        val allVideo : List<Video>? = listOf()*/
    )

    data class Article(
        @SerializedName("Id")
        @Expose
        val id: Int? = 0,
        @SerializedName("ArticleName")
        @Expose
        val articleName: String? = "",
        @SerializedName("Tags")
        @Expose
        val tags: String? = "",
        @SerializedName("ArticlePath")
        @Expose
        val articlePath: String? = "",
        @SerializedName("ArticleCode")
        @Expose
        val articleCode: String? = "",
        @SerializedName("ImgPath")
        @Expose
        val imgPath: String? = "",
        @SerializedName("IsBookMarked")
        @Expose
        val isBookMarked: Int? = 0,
    )

    data class Video(
        @SerializedName("id")
        @Expose
        val id: Int? = 0,
        @SerializedName("Title")
        @Expose
        val title: String? = "",
        @SerializedName("Link")
        @Expose
        val link: String? = "",
        @SerializedName("Path")
        @Expose
        val path: String? = "",
        @SerializedName("Description")
        @Expose
        val description: String? = "",
        @SerializedName("IsBookMarked")
        @Expose
        val isBookMarked: Int? = 0,
    )

}