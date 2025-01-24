package com.test.my.app.model.blogs

import com.test.my.app.model.BaseRequest
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogsListBySearchModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: JsonObject, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("search")
        val search: String = "",
        @SerializedName("page")
        val page: Int = 0,
        @SerializedName("per_page")
        val perPage: Int = 10
    )

    data class BlogsListBySearchResponse(
        @SerializedName("ListBySearchTerm")
        val listBySearchTerm: List<BlogModel.Blog> = listOf()
    )

}