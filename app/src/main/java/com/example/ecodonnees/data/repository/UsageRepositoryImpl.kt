package com.example.ecodonnees.data.repository

import com.example.ecodonnees.data.local.dao.UsageDao
import com.example.ecodonnees.data.local.entity.UsageEntity
import com.example.ecodonnees.domain.model.Usage
import com.example.ecodonnees.domain.repository.UsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val usageDao: UsageDao
) : UsageRepository {

    override fun getUsage(): Flow<Usage?> {
        return usageDao.getUsage().map { it?.toDomain() }
    }

    override suspend fun getUsageOnce(): Usage? {
        return usageDao.getUsageOnce()?.toDomain()
    }

    override suspend fun updateUsage(bytes: Long) {
        usageDao.updateUsage(bytes)
    }

    override suspend fun resetUsage(timestamp: Long) {
        usageDao.resetUsage(timestamp)
    }

    override suspend fun initializeUsage() {
        if (usageDao.getUsageOnce() == null) {
            usageDao.insertUsage(UsageEntity(1, 0, System.currentTimeMillis()))
        }
    }

    private fun UsageEntity.toDomain() = Usage(
        totalBytesUsed = totalBytesUsed,
        lastResetTimestamp = lastResetTimestamp
    )
}