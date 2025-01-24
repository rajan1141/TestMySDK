package com.test.my.app.model.shr

import com.test.my.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DownloadDocumentModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    @Transient
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val PersonID: String = "",
        @SerializedName("DocumentID")
        val DocumentID: String = ""
    )

    data class DownloadDocumentResponse(
        @SerializedName("HealthRelatedDocument")
        val healthRelatedDocument: HealthRelatedDocument = HealthRelatedDocument()
    )

    data class HealthRelatedDocument(
        @SerializedName("ID")
        val ID: String = "",
        @SerializedName("FileName")
        val FileName: String = "",
        @SerializedName("FileBytes")
        val FileBytes: String = ""
    )

}