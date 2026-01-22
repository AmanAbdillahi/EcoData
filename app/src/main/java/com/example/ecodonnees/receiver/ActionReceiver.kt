package com.example.ecodonnees.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.example.ecodonnees.presentation.MainActivity
import com.example.ecodonnees.service.BlockingVpnService
import com.example.ecodonnees.service.DataMonitoringForegroundService

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DataMonitoringForegroundService.ACTION_BLOCK -> {
                val blockIntent = Intent(context, DataMonitoringForegroundService::class.java).apply {
                    action = "FORCE_BLOCK"
                }
                context.startService(blockIntent)
            }
            DataMonitoringForegroundService.ACTION_UNBLOCK -> {
                val vpnIntent = VpnService.prepare(context)
                if (vpnIntent == null) {
                    val unblockIntent = Intent(context, DataMonitoringForegroundService::class.java).apply {
                        action = "FORCE_UNBLOCK"
                    }
                    context.startService(unblockIntent)
                }
            }
            DataMonitoringForegroundService.ACTION_SETTINGS -> {
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(mainIntent)
            }
        }
    }
}