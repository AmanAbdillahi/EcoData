package com.example.ecodonnees.domain.usecase

import com.example.ecodonnees.domain.repository.UsageRepository
import javax.inject.Inject

class ResetUsageUseCase @Inject constructor(
    private val usageRepository: UsageRepository
) {
    suspend operator fun invoke() {
        usageRepository.resetUsage(System.currentTimeMillis())
    }
}