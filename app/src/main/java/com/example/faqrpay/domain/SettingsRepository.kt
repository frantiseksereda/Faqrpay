package com.example.faqrpay.domain

import com.example.faqrpay.data.security.SecureSettingsManager

class SettingsRepository(private val secureSettings: SecureSettingsManager) {

    fun updateToken(newToken: String) = secureSettings.saveToken(newToken)

    fun getAuthToken(): String = secureSettings.getToken() ?: ""

    fun isPasswordCorrect(input: String): Boolean {
        val saved = secureSettings.getPassword()
        return saved == input
    }

    fun setAccountInfo(acc: String) = secureSettings.saveAccountNumber(acc)

    fun getAccountInfo(): String = secureSettings.getAccountNumber() ?: "Not Set"
}