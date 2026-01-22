package com.example.ecodonnees.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class UssdHelper(private val context: Context) {

    fun executeUssdCode(ussdCode: String, onResult: (Boolean, String) -> Unit) {
        try {
            val ussdCodeEncoded = Uri.encode(ussdCode)
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$ussdCodeEncoded")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)

            onResult(true, "Code USSD exécuté")
        } catch (e: Exception) {
            onResult(false, "Erreur lors de l'exécution du code USSD: ${e.message}")
        }
    }

    fun canMakePhoneCalls(): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.simState == TelephonyManager.SIM_STATE_READY
    }
}