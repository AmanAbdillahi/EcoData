package com.example.ecodonnees.service

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.example.ecodonnees.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.io.FileInputStream
import java.io.FileOutputStream

class BlockingVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceScope: CoroutineScope? = null

    companion object {
        const val ACTION_START = "com.example.ecodonnees.START_VPN"
        const val ACTION_STOP = "com.example.ecodonnees.STOP_VPN"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startVpn()
            ACTION_STOP -> stopVpnService()
        }
        return START_STICKY
    }

    private fun startVpn() {
        if (vpnInterface != null) return

        val configureIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        vpnInterface = Builder()
            .setSession("EcoDonnées VPN")
            .addAddress("10.0.0.2", 24)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .setBlocking(false)
            .setConfigureIntent(configureIntent)
            .establish()

        vpnInterface?.let {
            serviceScope = CoroutineScope(Dispatchers.IO + Job())
            notifyMonitoringService(true)
            handleTraffic(it)
        }
    }

    private fun handleTraffic(vpnInterface: ParcelFileDescriptor) {
        val inputStream = FileInputStream(vpnInterface.fileDescriptor)
        val outputStream = FileOutputStream(vpnInterface.fileDescriptor)
        val buffer = ByteArray(32767)

        Thread {
            try {
                while (true) {
                    val length = inputStream.read(buffer)
                    if (length <= 0) break
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun stopVpnService() {
        vpnInterface?.close()
        vpnInterface = null
        serviceScope?.cancel()
        serviceScope = null
        notifyMonitoringService(false)
        stopSelf()
    }

    private fun notifyMonitoringService(isActive: Boolean) {
        val intent = Intent(this, DataMonitoringForegroundService::class.java).apply {
            action = "UPDATE_VPN_STATUS"
            putExtra("isActive", isActive)
        }
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpnService()
    }

    override fun onRevoke() {
        super.onRevoke()
        stopVpnService()
    }
}