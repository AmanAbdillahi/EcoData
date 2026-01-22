package com.example.ecodonnees.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecodonnees.domain.model.DataStatus
import com.example.ecodonnees.domain.model.InternetPackage
import com.example.ecodonnees.domain.model.Quota
import com.example.ecodonnees.domain.repository.QuotaRepository
import com.example.ecodonnees.domain.repository.UsageRepository
import com.example.ecodonnees.domain.usecase.GetDataStatusUseCase
import com.example.ecodonnees.util.NetworkStatsReader
import com.example.ecodonnees.util.UssdHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ThemeMode {
    LIGHT, DARK, AUTO
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val quotaRepository: QuotaRepository,
    private val usageRepository: UsageRepository,
    private val networkStatsReader: NetworkStatsReader,
    getDataStatusUseCase: GetDataStatusUseCase
) : AndroidViewModel(application) {

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val ussdHelper = UssdHelper(application)

    val dataStatus: StateFlow<DataStatus?> = getDataStatusUseCase(false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveQuota(quotaBytes: Long, daysValid: Long) {
        viewModelScope.launch {
            val currentTimestamp = System.currentTimeMillis()
            val expiryTimestamp = currentTimestamp + (daysValid * 24 * 60 * 60 * 1000)

            usageRepository.resetUsage(currentTimestamp)

            quotaRepository.saveQuota(
                Quota(
                    quotaBytes = quotaBytes,
                    expiryTimestamp = expiryTimestamp,
                    isEnabled = true
                )
            )
        }
    }

    fun saveQuotaWithTimestamp(quotaBytes: Long, expiryTimestamp: Long) {
        viewModelScope.launch {
            val currentTimestamp = System.currentTimeMillis()

            usageRepository.resetUsage(currentTimestamp)

            quotaRepository.saveQuota(
                Quota(
                    quotaBytes = quotaBytes,
                    expiryTimestamp = expiryTimestamp,
                    isEnabled = true
                )
            )
        }
    }

    fun purchasePackage(package_: InternetPackage, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (!ussdHelper.canMakePhoneCalls()) {
                onResult(false, "Aucune carte SIM détectée")
                return@launch
            }

            ussdHelper.executeUssdCode(package_.ussdCode) { success, message ->
                if (success) {
                    viewModelScope.launch {
                        delay(2000)

                        val currentTimestamp = System.currentTimeMillis()
                        val expiryTimestamp = currentTimestamp + (package_.validityDays * 24L * 60 * 60 * 1000)
                        val quotaBytes = package_.dataGB * 1024L * 1024L * 1024L

                        usageRepository.resetUsage(currentTimestamp)

                        quotaRepository.saveQuota(
                            Quota(
                                quotaBytes = quotaBytes,
                                expiryTimestamp = expiryTimestamp,
                                isEnabled = true
                            )
                        )

                        onResult(true, "Forfait ${package_.name} activé")
                    }
                } else {
                    onResult(false, message)
                }
            }
        }
    }

    fun blockInternet() {
        viewModelScope.launch {
            val quota = quotaRepository.getQuotaOnce()
            if (quota != null) {
                quotaRepository.saveQuota(
                    quota.copy(expiryTimestamp = System.currentTimeMillis() - 1)
                )
            }
        }
    }

    fun unblockInternet() {
        viewModelScope.launch {
            val currentTimestamp = System.currentTimeMillis()
            usageRepository.resetUsage(currentTimestamp)
            val quota = quotaRepository.getQuotaOnce()
            if (quota != null) {
                quotaRepository.saveQuota(
                    quota.copy(expiryTimestamp = currentTimestamp + 86400000)
                )
            }
        }
    }

    fun resetUsage() {
        viewModelScope.launch {
            usageRepository.resetUsage(System.currentTimeMillis())
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun testDataUsage(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val usage = usageRepository.getUsageOnce()
            if (usage != null) {
                val bytes = networkStatsReader.getTotalMobileDataUsage(usage.lastResetTimestamp)
                val mb = bytes / (1024 * 1024)
                onResult("Données depuis reset: $bytes bytes ($mb MB)\nTimestamp reset: ${usage.lastResetTimestamp}")
            } else {
                onResult("Aucune donnée d'usage trouvée")
            }
        }
    }
}