package com.sereda.faqrpay.ui.qr

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sereda.faqrpay.domain.TransactionManager
import com.sereda.faqrpay.util.ServiceLocator
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import android.content.Context

class QRViewModel(private val transactionManager: TransactionManager) : ViewModel() {

    // Input state
    var amountInput by mutableStateOf("")
    var selectedCurrency by mutableStateOf("CZK")

    // Output state
    var qrBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentTransactionId by mutableStateOf<String?>(null)
    var isPaidSuccessfully by mutableStateOf(false)
    var cooldownSeconds by mutableStateOf(0)
    var validationAttempted by mutableStateOf(false)

    /**
     * Triggers the creation of a transaction in DB and generation of the QR code
     */
    fun generatePayment() {
        val amount = amountInput.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            errorMessage = "Zadejte platnou částku"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            // Call the TransactionManager we built earlier
            val result = transactionManager.createTransactionAndQrCode(amount, selectedCurrency)

            if (result != null) {
                currentTransactionId = result.first
                qrBitmap = result.second
            } else {
                errorMessage = "Chyba při generování QR kódu. Zkontrolujte IBAN v nastavení."
            }

            isLoading = false
        }
    }

    fun clearQr() {
        qrBitmap = null
        amountInput = ""
    }

    fun validateStatus() {
        val id = currentTransactionId ?: return

        viewModelScope.launch {
            isLoading = true
            val success = transactionManager.validatePayment(id)
            isPaidSuccessfully = success
            validationAttempted = true
            isLoading = false

            if (!success) {
                startCooldown()
            }
        }
    }

    private fun startCooldown() {
        viewModelScope.launch {
            cooldownSeconds = 31
            while (cooldownSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                cooldownSeconds--
            }
        }
    }

    fun reset() {
        amountInput = ""
        qrBitmap = null
        currentTransactionId = null
        isPaidSuccessfully = false
        validationAttempted = false
        cooldownSeconds = 0
    }
}

class QRViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val manager = ServiceLocator.provideTransactionManager(context)
        return QRViewModel(manager) as T
    }
}