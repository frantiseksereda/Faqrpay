package com.sereda.faqrpay.domain

import com.sereda.faqrpay.data.security.SecureSettingsManager
import java.time.LocalDate
import com.sereda.faqrpay.data.network.FioApiService
class SettingsRepository(
    private val secureSettings: SecureSettingsManager,
    private val apiService: FioApiService
) {

    fun updateToken(newToken: String) = secureSettings.saveToken(newToken)
    fun getAuthToken(): String = secureSettings.getToken() ?: ""
    fun isPasswordCorrect(input: String): Boolean {
        val saved = secureSettings.getPassword()
        return saved == input
    }

    fun setAccountInfo(acc: String) = secureSettings.saveAccountNumber(acc)
    fun getAccountInfo(): String = secureSettings.getAccountNumber() ?: "Nenastaveno"
    fun setPassword(pass: String) = secureSettings.savePassword(pass)
    fun isPasswordNull(): Boolean = secureSettings.getPassword().isNullOrBlank()
    fun getIban(): String = secureSettings.getIban() ?: "Nenastaveno"

    suspend fun updateTokenAndFetchAccount(newToken: String): Result<Unit> {
        return try {
            // 1. Save the token
            secureSettings.saveToken(newToken)

            // 2. Prepare today's date (YYYY-MM-DD)
            val today = LocalDate.now().toString()

            // 3. Fetch from Fio
            val response = apiService.getTransactions(
                token = newToken,
                dateStart = today,
                dateEnd = today
            )
            // 4. Extract and save the account ID
            val accountId = response.accountStatement.info.accountId
            val iban = response.accountStatement.info.iban
            secureSettings.saveAccountNumber(accountId)
            secureSettings.saveIban(iban)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}