package com.example.faqrpay.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface FioApiService {
    @GET("periods/{token}/{dateStart}/{dateEnd}/transactions.json")
    suspend fun getTransactions(
        @Path("token") token: String,
        @Path("dateStart") dateStart: String,
        @Path("dateEnd") dateEnd: String
    ): FioResponse
}