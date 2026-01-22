package com.example.ecodonnees.data.repository

import com.example.ecodonnees.data.local.dao.QuotaDao
import com.example.ecodonnees.data.local.entity.QuotaEntity
import com.example.ecodonnees.domain.model.Quota
import com.example.ecodonnees.domain.repository.QuotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuotaRepositoryImpl @Inject constructor(
    private val quotaDao: QuotaDao
) : QuotaRepository {

    override fun getQuota(): Flow<Quota?> {
        return quotaDao.getQuota().map { it?.toDomain() }
    }

    override suspend fun getQuotaOnce(): Quota? {
        return quotaDao.getQuotaOnce()?.toDomain()
    }

    override suspend fun saveQuota(quota: Quota) {
        quotaDao.insertQuota(quota.toEntity())
    }

    override suspend fun updateEnabled(enabled: Boolean) {
        quotaDao.updateEnabled(enabled)
    }

    private fun QuotaEntity.toDomain() = Quota(
        quotaBytes = quotaBytes,
        expiryTimestamp = expiryTimestamp,
        isEnabled = isEnabled
    )

    private fun Quota.toEntity() = QuotaEntity(
        id = 1,
        quotaBytes = quotaBytes,
        expiryTimestamp = expiryTimestamp,
        isEnabled = isEnabled
    )
}