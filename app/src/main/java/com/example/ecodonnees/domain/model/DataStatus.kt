package com.example.ecodonnees.domain.model

data class DataStatus(
    val usedBytes: Long,
    val quotaBytes: Long,
    val remainingBytes: Long,
    val percentageUsed: Float,
    val expiryTimestamp: Long,
    val isBlocked: Boolean,
    val isVpnActive: Boolean,
    val timeRemainingMs: Long
)