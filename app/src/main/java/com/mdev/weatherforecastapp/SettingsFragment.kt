package com.mdev.weatherforecastapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch

class SettingsFragment : Fragment() {

    private lateinit var lightModeSwitch: Switch
    private lateinit var pushNotificationSwitch: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)
        val context = requireContext()

        // Initialize switches
        lightModeSwitch = view.findViewById(R.id.lightModeSwitch)
        pushNotificationSwitch = view.findViewById(R.id.pushNotificationSwitch)
        val containerLayout = view.findViewById<LinearLayout>(R.id.setting_layout)

        // Add listeners for switch changes if needed
        lightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle light mode switch change
            if (isChecked) {
                // Light mode enabled
                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("lightModeEnabled", isChecked)
                editor.apply()

                containerLayout.setBackgroundColor(Color.BLACK)

            } else {
                // Light mode disabled
                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("lightModeEnabled", isChecked)
                editor.apply()

                containerLayout.setBackgroundColor(Color.WHITE)
            }
        }

        pushNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle push notification switch change
            if (isChecked) {
                // Opt-in for push notifications
                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("notificationEnabled", isChecked)
                editor.apply()
            } else {
                // Opt-out from push notifications
                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("notificationEnabled", isChecked)
                editor.apply()
            }
        }

        return view;
    }

}