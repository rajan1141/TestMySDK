package com.test.my.app.home.common

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface DownloadReportApiService {
    @GET
    fun downloadHraReport(@Url fileUrl: String): Call<ResponseBody>
}