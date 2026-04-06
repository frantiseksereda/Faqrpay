package com.example.faqrpay.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecureSettingsManager(context: Context) {

    // 1. Get the hardware-backed Master Key using the modern helper
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // 2. Initialize using the Alias directly
    private val sharedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        "faqr_secure_prefs", // Filename
        masterKeyAlias,      // Use the Alias String directly
        context,             // Context comes 3rd now in some versions
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // --- YOUR FUNCTIONS REMAIN THE SAME ---
    fun saveToken(token: String) {
        sharedPrefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? = sharedPrefs.getString("auth_token", null)

    fun savePassword(pw: String) {
        sharedPrefs.edit().putString("app_password", pw).apply()
    }

    fun getPassword(): String? = sharedPrefs.getString("app_password", null)

    fun saveAccountNumber(accountNumber: String) {
        sharedPrefs.edit().putString("accountNumber", accountNumber).apply()
    }

    fun getAccountNumber(): String? = sharedPrefs.getString("accountNumber", null)

    fun saveIban(iban: String) {
        sharedPrefs.edit().putString("iban", iban).apply()
    }

    fun getIban(): String? = sharedPrefs.getString("iban", null)
}
