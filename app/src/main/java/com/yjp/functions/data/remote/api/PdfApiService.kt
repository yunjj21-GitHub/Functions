package com.yjp.functions.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface PdfApiService {

    @Streaming
    @GET("site/main/file/download/uu/0c8798275ccf40d7801ec2d6c686146e")
    suspend fun downloadPdf(): Response<ResponseBody>
}
