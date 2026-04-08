package com.sereda.faqrpay.data.network

import kotlinx.serialization.Serializable

@Serializable
data class FioResponse(val accountStatement: AccountStatement)

@Serializable
data class AccountStatement(val info: AccountInfo)

@Serializable
data class AccountInfo(
    val accountId: String,
    val iban: String)