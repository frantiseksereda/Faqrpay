package com.example.faqrpay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String, // Your "Static + UUID" string
    val date: LocalDateTime,             // Store as Long (Unix Timestamp) for easy sorting
    val amount: Double,
    val currency: String
)