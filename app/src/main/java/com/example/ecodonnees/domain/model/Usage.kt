package com.example.ecodonnees.domain.model

data class Usage(
    val totalBytesUsed: Long,
    val lastResetTimestamp: Long
)