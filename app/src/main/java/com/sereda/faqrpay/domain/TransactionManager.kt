package com.sereda.faqrpay.domain

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionManager(
    private val transactionRepo: TransactionRepository,
    private val settingsRepo: SettingsRepository
) {

    /**
     * Orchestrates the creation of a transaction and its corresponding QR code.
     * Returns a Bitmap that can be displayed in the UI.
     */
    suspend fun createTransactionAndQrCode(amount: Double, currency: String): Pair<String, Bitmap>? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Get IBAN from settings
                val iban = settingsRepo.getIban()

                // 2. Check for empty, null, or the "Not Set" placeholder
                if (iban.isBlank() || iban == "Nenastaveno") {
                    // We throw an exception so the 'catch' block handles it
                    throw IllegalStateException("Chybí číslo účtu. Nastavte API token v nastavení.")
                }

                // 3. Create record in DB and get new transactionId
                val transactionId = transactionRepo.addTransaction(amount, currency)

                // 4. Create SPD (Short Payment Descriptor) string
                // Format: SPD*1.0*ACC:{iban}*AM:{amount}*CC:{currency}*MSG:{id}*PT:IP
                val spdString = "SPD*1.0*ACC:$iban*AM:${"%.2f".format(amount)}*CC:$currency*MSG:$transactionId*PT:IP"

                // 5. Generate and return QR code bitmap
                val bitmap = generateQrCode(spdString)

                Pair(transactionId, bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun generateQrCode(text: String): Bitmap {
        val width = 512
        val height = 512
        val matrix: BitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            width,
            height
        )

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    suspend fun validatePayment(transactionId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = settingsRepo.getAuthToken()
                val today = java.time.LocalDate.now().toString()

                // We use a raw ResponseBody call here because we don't need to parse
                // the whole transaction list into objects; we just need the raw text.
                val response = transactionRepo.getRawTransactions(token, today, today)

                if (response.isSuccessful) {
                    val jsonString = response.body()?.string() ?: ""

                    // Check if our unique ID exists in the transaction list
                    if (jsonString.contains(transactionId)) {
                        transactionRepo.markAsPaid(transactionId)
                        return@withContext true
                    }
                }
                false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}