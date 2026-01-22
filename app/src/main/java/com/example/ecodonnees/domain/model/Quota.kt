package com.example.ecodonnees.domain.model

data class Quota(
    val quotaBytes: Long,
    val expiryTimestamp: Long,
    val isEnabled: Boolean
)