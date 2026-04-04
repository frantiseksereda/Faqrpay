package com.example.faqrpay.util

import android.content.Context
import com.example.faqrpay.data.security.SecureSettingsManager

class ServiceLocator {
    companion object {
        @Volatile private var secureInstance: SecureSettingsManager? = null

        fun getSecureSettings(context: Context): SecureSettingsManager =
            secureInstance ?: synchronized(this) {
                secureInstance ?: SecureSettingsManager(context.applicationContext).also { secureInstance = it }
            }
    }
}