package com.example.ecodonnees.domain.repository

import com.example.ecodonnees.domain.model.Quota
import kotlinx.coroutines.flow.Flow

interface QuotaRepository {
    fun getQuota(): Flow<Quota?>
    suspend fun getQuotaOnce(): Quota?
    suspend fun saveQuota(quota: Quota)
    suspend fun updateEnabled(enabled: Boolean)
}