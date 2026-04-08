package com.sereda.faqrpay.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface FioApiService {
    @GET("periods/{token}/{dateStart}/{dateEnd}/transactions.json")
    suspend fun getTransactions(
        @Path("token") token: String,
        @Path("dateStart") dateStart: String,
        @Path("dateEnd") dateEnd: String
    ): FioResponse

    @Streaming // Useful for raw bodies to prevent loading everything into RAM at once
    @GET("periods/{token}/{dateStart}/{dateEnd}/transactions.json")
    suspend fun getRawTransactions(
        @Path("token") token: String,
        @Path("dateStart") dateStart: String,
        @Path("dateEnd") dateEnd: String
    ): Response<ResponseBody>
}