package com.example.faqrpay.domain

import com.example.faqrpay.data.local.dao.TransactionDao
import com.example.faqrpay.data.local.entity.TransactionEntity
import com.example.faqrpay.data.network.FioApiService
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val apiService: FioApiService
) {

    // --- READ (Get data for the UI) ---
    // We return a Flow so the UI updates automatically when data changes
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    // --- CREATE ---
    suspend fun addTransaction(amount: Double, currency: String): String {
        val newTransaction = TransactionEntity(
            id = "FAQR-TXN-${UUID.randomUUID()}", // fixed string + UUID
            date = LocalDateTime.now(),           // Current timestamp
            amount = amount,
            currency = currency,
            isPaid = false                  // Default status
        )
        transactionDao.insertTransaction(newTransaction)

        return newTransaction.id
    }

    // ---GET ONE ---
    suspend fun getTransactionById(id: String): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }

    // --- UPDATE (Mark as Paid) ---
    suspend fun markAsPaid(transactionId: String) {
        transactionDao.markAsPaid(transactionId)
    }

    // --- DELETE ---
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        // transactionDao.delete(transaction)
    }

    suspend fun getRawTransactions(token: String, start: String, end: String) =
        apiService.getRawTransactions(token, start, end)

}