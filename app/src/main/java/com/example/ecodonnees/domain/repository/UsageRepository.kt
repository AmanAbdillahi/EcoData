package com.example.ecodonnees.domain.repository

import com.example.ecodonnees.domain.model.Usage
import kotlinx.coroutines.flow.Flow

interface UsageRepository {
    fun getUsage(): Flow<Usage?>
    suspend fun getUsageOnce(): Usage?
    suspend fun updateUsage(bytes: Long)
    suspend fun resetUsage(timestamp: Long)
    suspend fun initializeUsage()
}