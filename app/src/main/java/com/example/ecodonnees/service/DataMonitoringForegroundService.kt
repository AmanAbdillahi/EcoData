package com.example.ecodonnees.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ecodonnees.R
import com.example.ecodonnees.domain.model.DataStatus
import com.example.ecodonnees.domain.repository.QuotaRepository
import com.example.ecodonnees.domain.repository.UsageRepository
import com.example.ecodonnees.domain.usecase.GetDataStatusUseCase
import com.example.ecodonnees.presentation.MainActivity
import com.example.ecodonnees.receiver.ActionReceiver
import com.example.ecodonnees.util.NetworkStatsReader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DataMonitoringForegroundService : Service() {

    @Inject lateinit var quotaRepository: QuotaRepository
    @Inject lateinit var usageRepository: UsageRepository
    @Inject lateinit var getDataStatusUseCase: GetDataStatusUseCase
    @Inject lateinit var networkStatsReader: NetworkStatsReader

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var notificationManager: NotificationManager

    private val _dataStatus = MutableStateFlow<DataStatus?>(null)
    val dataStatus: StateFlow<DataStatus?> = _dataStatus.asStateFlow()

    private var isVpnActive = false

    companion object {
        const val CHANNEL_ID = "data_monitoring_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_BLOCK = "com.example.ecodonnees.ACTION_BLOCK"
        const val ACTION_UNBLOCK = "com.example.ecodonnees.ACTION_UNBLOCK"
        const val ACTION_SETTINGS = "com.example.ecodonnees.ACTION_SETTINGS"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(null))
        startMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "UPDATE_VPN_STATUS" -> {
                isVpnActive = intent.getBooleanExtra("isActive", false)
                updateNotification()
            }
            "FORCE_BLOCK" -> {
                serviceScope.launch {
                    val quota = quotaRepository.getQuotaOnce()
                    if (quota != null) {
                        quotaRepository.saveQuota(quota.copy(expiryTimestamp = System.currentTimeMillis() - 1))
                    }
                }
            }
            "FORCE_UNBLOCK" -> {
                serviceScope.launch {
                    val usage = usageRepository.getUsageOnce()
                    if (usage != null) {
                        usageRepository.resetUsage(System.currentTimeMillis())
                    }
                    val quota = quotaRepository.getQuotaOnce()
                    if (quota != null) {
                        quotaRepository.saveQuota(
                            quota.copy(expiryTimestamp = System.currentTimeMillis() + 86400000)
                        )
                    }
                }
                val stopIntent = Intent(this, BlockingVpnService::class.java)
                stopIntent.action = BlockingVpnService.ACTION_STOP
                startService(stopIntent)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Surveillance des données",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notification permanente de surveillance"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun startMonitoring() {
        serviceScope.launch {
            usageRepository.initializeUsage()

            launch {
                getDataStatusUseCase(isVpnActive).collect { status ->
                    _dataStatus.value = status
                    status?.let { evaluateAndActOnQuota(it) }
                }
            }

            while (true) {
                updateDataUsage()
                updateNotification()
                delay(5000)
            }
        }
    }

    private suspend fun updateDataUsage() {
        val usage = usageRepository.getUsageOnce()
        if (usage != null) {
            val bytesUsed = networkStatsReader.getTotalMobileDataUsage(usage.lastResetTimestamp)
            usageRepository.updateUsage(bytesUsed)
        }
    }

    private fun evaluateAndActOnQuota(status: DataStatus) {
        if (status.isBlocked && !isVpnActive) {
            startVpn()
        } else if (!status.isBlocked && isVpnActive) {
            stopVpn()
        }
    }

    private fun startVpn() {
        val intent = Intent(this, BlockingVpnService::class.java)
        intent.action = BlockingVpnService.ACTION_START
        startService(intent)
        isVpnActive = true
    }

    private fun stopVpn() {
        val intent = Intent(this, BlockingVpnService::class.java)
        intent.action = BlockingVpnService.ACTION_STOP
        startService(intent)
        isVpnActive = false
    }

    private fun updateNotification() {
        val notification = buildNotification(_dataStatus.value)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(status: DataStatus?): Notification {
        val mainIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("EcoDonnées")
            .setContentIntent(mainIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (status != null) {
            val usedMB = status.usedBytes / (1024 * 1024)
            val quotaMB = status.quotaBytes / (1024 * 1024)
            val remainingMB = status.remainingBytes / (1024 * 1024)
            val percentage = status.percentageUsed.toInt()

            val timeRemaining = formatTimeRemaining(status.timeRemainingMs)
            val internetStatus = if (status.isBlocked) "🔴 Bloqué" else "🟢 Autorisé"
            val vpnStatus = if (status.isVpnActive) "Actif" else "Inactif"

            builder.setContentText("$usedMB / $quotaMB MB ($percentage%)")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "📊 Utilisé: $usedMB MB\n" +
                                "💾 Restant: $remainingMB MB\n" +
                                "📈 Consommé: $percentage%\n" +
                                "⏱️ Expire dans: $timeRemaining\n" +
                                "🌐 Internet: $internetStatus\n" +
                                "🔒 VPN: $vpnStatus"
                    )
                )

            if (status.isBlocked) {
                builder.addAction(
                    R.drawable.ic_unlock,
                    "Réactiver",
                    createActionIntent(ACTION_UNBLOCK)
                )
            } else {
                builder.addAction(
                    R.drawable.ic_lock,
                    "Bloquer",
                    createActionIntent(ACTION_BLOCK)
                )
            }

            builder.addAction(
                R.drawable.ic_settings,
                "Paramètres",
                createActionIntent(ACTION_SETTINGS)
            )
        } else {
            builder.setContentText("Initialisation...")
        }

        return builder.build()
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(this, ActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun formatTimeRemaining(ms: Long): String {
        if (ms <= 0) return "Expiré"
        val hours = ms / (1000 * 60 * 60)
        val days = hours / 24
        return when {
            days > 0 -> "${days}j"
            hours > 0 -> "${hours}h"
            else -> "<1h"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch {
            serviceScope.coroutineContext[Job]?.cancel()
        }
    }
}