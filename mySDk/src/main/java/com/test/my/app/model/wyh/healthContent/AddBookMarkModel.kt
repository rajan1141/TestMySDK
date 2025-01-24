package com.test.my.app.model.wyh.healthContent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddBookMarkModel {

    data class AddBookMarkRequest(
        @Expose
        @SerializedName("articleCode")
        val articleCode : String? = "",
        @SerializedName("isBookMarked")
        @Expose
        val isBookMarked : Boolean = false
    )

    data class AddBookMarkResponse(
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
        @SerializedName("articleCode")
        @Expose
        val articleCode: String? = ""
    )

}