package com.example.demo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class CallStatusService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
        registerPhoneStateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPhoneStateListener()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Call Status Service")
            .setContentText("Running in the background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun registerPhoneStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                // Handle phone call state changes here
                when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Phone call is idle
                        Log.e("TAG", "onCallStateChanged: CALL_STATE_IDLE", )
                        Toast.makeText(applicationContext," CALL_STATE_IDLE",Toast.LENGTH_SHORT)
                    }
                    TelephonyManager.CALL_STATE_RINGING -> {
                        Log.e("TAG", "onCallStateChanged: CALL_STATE_RINGING", )
                        Toast.makeText(applicationContext," CALL_STATE_RINGING",Toast.LENGTH_SHORT)
                        // Phone call is ringing
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Phone call is in progress
                        Log.e("TAG", "onCallStateChanged: CALL_STATE_OFFHOOK", )
                        Toast.makeText(applicationContext," CALL_STATE_OFFHOOK",Toast.LENGTH_SHORT)
                    }
                }
            }
        }
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun unregisterPhoneStateListener() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    companion object {
        private const val CHANNEL_ID = "call_status_channel"
        private const val CHANNEL_NAME = "Call Status"
        private const val NOTIFICATION_ID = 1
    }
}
