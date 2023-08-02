package com.mdev.weatherforecastapp

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var lightModeSwitch: Switch
    private lateinit var pushNotificationSwitch: Switch
    private lateinit var containerLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize switches
        lightModeSwitch = findViewById(R.id.lightModeSwitch)
        pushNotificationSwitch = findViewById(R.id.pushNotificationSwitch)
        containerLayout = findViewById(R.id.setting_layout)

        // Restore the previous switch states
        lightModeSwitch.isChecked = sharedPreferences.getBoolean("lightModeEnabled", false)
        pushNotificationSwitch.isChecked = sharedPreferences.getBoolean("notificationEnabled", false)

        // Add listeners for switch changes
        lightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle light mode switch change
            val editor = sharedPreferences.edit()
            editor.putBoolean("lightModeEnabled", isChecked)
            editor.apply()

            if (isChecked) {
                containerLayout.setBackgroundColor(Color.BLACK)
                changeTextViewsColor(findViewById(android.R.id.content), Color.WHITE)
            } else {
                containerLayout.setBackgroundColor(Color.WHITE)
                changeTextViewsColor(findViewById(android.R.id.content), Color.BLACK)
            }
        }

        pushNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle push notification switch change
            val editor = sharedPreferences.edit()
            editor.putBoolean("notificationEnabled", isChecked)
            editor.apply()
        }
    }


    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lightModeEnabled = sharedPreferences.getBoolean("lightModeEnabled", false)

        val containerLayout = findViewById<LinearLayout>(R.id.setting_layout)

        if (lightModeEnabled) {
            containerLayout.setBackgroundColor(Color.BLACK)
            changeTextViewsColor(findViewById(android.R.id.content), Color.WHITE)
        } else {
            containerLayout.setBackgroundColor(Color.WHITE)
            changeTextViewsColor(findViewById(android.R.id.content), Color.BLACK)
        }
    }

    private fun changeTextViewsColor(view: View, color: Int) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                changeTextViewsColor(child, color)
            }
        } else if (view is TextView) {
            // Change the text color to white
            view.setTextColor(color)
        }
    }
}
