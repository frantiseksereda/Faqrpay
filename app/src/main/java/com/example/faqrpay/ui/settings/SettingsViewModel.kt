package com.example.faqrpay.ui.settings

import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.faqrpay.domain.SettingsRepository

class SettingsViewModel(private val settingsRepo: SettingsRepository) : ViewModel() {

    // Possible states for our UI
    enum class AuthState { SETUP, LOCKED, UNLOCKED }

    var authState by mutableStateOf(AuthState.LOCKED)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        // If password is null or empty, we need SETUP
        authState = if (settingsRepo.isPasswordNull()) {
            AuthState.SETUP
        } else {
            AuthState.LOCKED
        }
    }

    fun setupNewPassword(pw: String, confirmPw: String) {
        // 1. Check if empty
        if (pw.isBlank() || confirmPw.isBlank()) {
            errorMessage = "Heslo nesmí být prázdné"
            return
        }

        // 2. Check if they match
        if (pw != confirmPw) {
            errorMessage = "Hesla se neshodují"
            return
        }

        // 3. Check length (optional but recommended)
        if (pw.length < 4) {
            errorMessage = "Heslo musí mít alespoň 4 znaky"
            return
        }

        // 4. Success! Save it
        settingsRepo.setPassword(pw)
        errorMessage = null
        authState = AuthState.UNLOCKED // Move into the settings
    }

    fun unlock(input: String) {
        if (settingsRepo.isPasswordCorrect(input)) {
            authState = AuthState.UNLOCKED
            errorMessage = null
        } else {
            errorMessage = "Nesprávné heslo"
        }
    }


    // State for existing values (Loaded from Secure Storage)
    var currentAccount by mutableStateOf(settingsRepo.getAccountInfo())
        private set

    var currentToken by mutableStateOf(settingsRepo.getAuthToken())
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun saveSettings(token: String) {
        viewModelScope.launch {
            isLoading = true // Add this state to show a spinner
            val result = settingsRepo.updateTokenAndFetchAccount(token)

            if (result.isSuccess) {
                currentToken = settingsRepo.getAuthToken()
                currentAccount = settingsRepo.getAccountInfo()
                successMessage = "Token uložen a získáno číslo účtu"
            } else {
                errorMessage = "Nepodařilo se ověřit token: ${result.exceptionOrNull()?.message}"
            }
            isLoading = false
        }
    }
}