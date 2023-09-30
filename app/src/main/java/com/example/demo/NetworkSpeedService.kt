package com.example.demo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class NetworkSpeedService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable { detectNetworkSpeed() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
//        Log.e("TAG", "onStartCommand: ", )
        handler.postDelayed(runnable, 1000) // Update network speed every 1 second
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val channelId = "network_speed_channel"
        val channelName = "Network Speed"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Network Speed")
            .setContentText("Detecting network speed...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1, notification)
    }

    private fun detectNetworkSpeed() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val downloadSpeed = networkCapabilities?.linkDownstreamBandwidthKbps
        val uploadSpeed = networkCapabilities?.linkUpstreamBandwidthKbps

        val notificationText = "Download Speed: $downloadSpeed Kbps\nUpload Speed: $uploadSpeed Kbps"

        val notification = NotificationCompat.Builder(this, "network_speed_channel")
            .setContentTitle("Network Speed")
            .setContentText(notificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        notificationManager.notify(1, notification)

//        Log.e("TAG", "detectNetworkSpeed: "+"Download Speed: $downloadSpeed Kbps\nUpload Speed: $uploadSpeed Kbps" )

        handler.postDelayed(runnable, 1000) // Update network speed every 1 second

    }
}
