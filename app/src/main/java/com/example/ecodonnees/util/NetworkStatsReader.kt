package com.example.ecodonnees.util

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.RemoteException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkStatsReader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val networkStatsManager: NetworkStatsManager =
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    fun getTotalMobileDataUsage(startTimestamp: Long): Long {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return 0L
            }

            val endTime = System.currentTimeMillis()

            var totalRx = 0L
            var totalTx = 0L

            try {
                val networkStats: NetworkStats? = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE,
                    null,
                    startTimestamp,
                    endTime
                )

                val bucket = NetworkStats.Bucket()
                while (networkStats?.hasNextBucket() == true) {
                    networkStats.getNextBucket(bucket)
                    totalRx += bucket.rxBytes
                    totalTx += bucket.txBytes
                }
                networkStats?.close()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

            totalRx + totalTx
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}