package com.example.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    private lateinit var receiver: BroadcastReceiver



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Create a new instance of the BroadcastReceiver
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Check if the phone is on
                if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
                    // Start the foreground service
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(Intent(context, MyForegroundService::class.java))
//                        startService(Intent(context, IncomingCallReceiver::class.java))
                    } else {
                        startService(Intent(context, MyForegroundService::class.java))
//                        startService(Intent(context, IncomingCallReceiver::class.java))
                    }
                }
            }
        }

        // Register the BroadcastReceiver to listen for the ACTION_BOOT_COMPLETED intent
        val filter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        registerReceiver(receiver, filter)

        val intent = Intent(this, NetworkSpeedService::class.java)
        this.startService(intent)

        startForegroundService(Intent(this,CallStatusService::class.java))
        startService(Intent(this,CallStatusService::class.java))


    }
}