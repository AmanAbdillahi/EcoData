package com.example.ecodonnees.domain.usecase

import com.example.ecodonnees.domain.model.DataStatus
import com.example.ecodonnees.domain.repository.QuotaRepository
import com.example.ecodonnees.domain.repository.UsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDataStatusUseCase @Inject constructor(
    private val quotaRepository: QuotaRepository,
    private val usageRepository: UsageRepository
) {
    operator fun invoke(isVpnActive: Boolean): Flow<DataStatus?> {
        return combine(
            quotaRepository.getQuota(),
            usageRepository.getUsage()
        ) { quota, usage ->
            if (quota == null || usage == null) return@combine null

            val remaining = (quota.quotaBytes - usage.totalBytesUsed).coerceAtLeast(0)
            val percentage = if (quota.quotaBytes > 0) {
                (usage.totalBytesUsed.toFloat() / quota.quotaBytes.toFloat() * 100f).coerceIn(0f, 100f)
            } else 0f

            val currentTime = System.currentTimeMillis()
            val timeRemaining = (quota.expiryTimestamp - currentTime).coerceAtLeast(0)

            val isBlocked = quota.isEnabled && (
                    usage.totalBytesUsed >= quota.quotaBytes ||
                            currentTime >= quota.expiryTimestamp
                    )

            DataStatus(
                usedBytes = usage.totalBytesUsed,
                quotaBytes = quota.quotaBytes,
                remainingBytes = remaining,
                percentageUsed = percentage,
                expiryTimestamp = quota.expiryTimestamp,
                isBlocked = isBlocked,
                isVpnActive = isVpnActive,
                timeRemainingMs = timeRemaining
            )
        }
    }
}