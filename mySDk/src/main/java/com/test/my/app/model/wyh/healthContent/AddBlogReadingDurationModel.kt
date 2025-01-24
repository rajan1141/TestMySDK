package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddBlogReadingDurationModel {

    data class AddBlogReadingDurationRequest(
        @Expose
        @SerializedName("blogId")
        val blogId : Int? = 0,
        @SerializedName("articleCode")
        @Expose
        val articleCode : String? = "",
        @Expose
        @SerializedName("readingmins")
        val readingmins : Int? = 0,
        @SerializedName("userId")
        @Expose
        val userId : String? = ""
    )

    data class AddBlogReadingDurationResponse(
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
        @SerializedName("blogId")
        @Expose
        val blogId: Int? = 0,
        @SerializedName("readingmins")
        @Expose
        val readingmins: Int? = 0,
        @SerializedName("userId")
        @Expose
        val userId: String? = "",
        @SerializedName("articleCode")
        @Expose
        val articleCode: String? = ""
    )

}