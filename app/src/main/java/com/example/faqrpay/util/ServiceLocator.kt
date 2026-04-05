package com.example.faqrpay.util

import android.content.Context
import com.example.faqrpay.data.security.SecureSettingsManager
import com.example.faqrpay.data.network.FioApiService
import com.example.faqrpay.domain.SettingsRepository
import com.example.faqrpay.domain.TransactionRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


class ServiceLocator {
    companion object {
        @Volatile
        private var secureInstance: SecureSettingsManager? = null

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://fioapi.fio.cz/v1/rest/")
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        }

        val fioApiService by lazy { retrofit.create(FioApiService::class.java) }


        fun getSecureSettings(context: Context): SecureSettingsManager =
            secureInstance ?: synchronized(this) {
                secureInstance ?: SecureSettingsManager(context.applicationContext).also {
                    secureInstance = it
                }
            }
    }
}