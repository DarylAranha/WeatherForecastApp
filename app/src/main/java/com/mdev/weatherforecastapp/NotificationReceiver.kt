package com.mdev.weatherforecastapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve temperature data if passed
        val temperature = intent.getDoubleExtra("temperature", 0.0)

        // Call the function to show the notification here
        showTemperatureUpdateNotification(context, temperature)
    }

    private fun showTemperatureUpdateNotification(context: Context, temp: Double) {
        // ... Your existing notification code ...
    }
}