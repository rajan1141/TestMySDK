package com.test.my.app.model

import com.test.my.app.model.blogs.BlogsCategoryModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class BaseHandlerResponse(
    @SerializedName(value = "JSONData", alternate = ["jsonData"])
    @Expose
    var jsonData: Any? = null,
    @SerializedName("JObject")
    @Expose
    var jObject: Any? = null,
    @SerializedName("StatusCode")
    @Expose
    var statusCode: String? = "",
    @SerializedName("ErrorNumber")
    @Expose
    var errorNumber: String? = "",
    @SerializedName("Message")
    @Expose
    var message: String? = "",
    @SerializedName("ProfileImageID")
    @Expose
    var profileImageID: String? = "",
    @SerializedName("CategoryList")
    @Expose
    val categoryList: List<BlogsCategoryModel.Category>? = listOf()

)