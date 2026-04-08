package com.sereda.faqrpay.util

import android.content.Context
import com.sereda.faqrpay.data.local.AppDatabase
import com.sereda.faqrpay.data.security.SecureSettingsManager
import com.sereda.faqrpay.data.network.FioApiService
import com.sereda.faqrpay.domain.SettingsRepository
import com.sereda.faqrpay.domain.TransactionManager
import com.sereda.faqrpay.domain.TransactionRepository
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

        fun provideTransactionRepository(context: Context): TransactionRepository {
            return TransactionRepository(
                transactionDao = AppDatabase.getDatabase(context).transactionDao(),
                apiService = fioApiService
            )
        }

        fun provideSettingsRepository(context: Context): SettingsRepository {
            // Build the settings repo using the secure storage and network service
            return SettingsRepository(
                secureSettings = getSecureSettings(context),
                apiService = fioApiService
            )
        }

        fun provideTransactionManager(context: Context): TransactionManager {
            return TransactionManager(
                transactionRepo = provideTransactionRepository(context),
                settingsRepo = provideSettingsRepository(context)
            )
        }
    }
}