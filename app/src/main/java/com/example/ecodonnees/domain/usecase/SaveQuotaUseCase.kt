package com.example.ecodonnees.domain.usecase

import com.example.ecodonnees.domain.model.Quota
import com.example.ecodonnees.domain.repository.QuotaRepository
import javax.inject.Inject

class SaveQuotaUseCase @Inject constructor(
    private val quotaRepository: QuotaRepository
) {
    suspend operator fun invoke(quota: Quota) {
        quotaRepository.saveQuota(quota)
    }
}