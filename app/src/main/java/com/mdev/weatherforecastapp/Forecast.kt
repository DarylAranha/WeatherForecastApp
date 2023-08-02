package com.mdev.weatherforecastapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject

class Forecast : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        // Retrieve the JSON data from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val jsonData = sharedPreferences.getString("weatherResponse", null)

        if (jsonData != null) {
            // Parse the JSON string to a JSONObject
            val jsonObject = JSONObject(jsonData)


            // Extract individual fields from the JSON data
            val main = jsonObject.getJSONObject("main")
//            val wind = jsonObject.getJSONObject("wind")

            // Find the TextView elements in the layout
            val tempTextView = findViewById<TextView>(R.id.tempTextView)
//            val feelsLikeTextView = findViewById<TextView>(R.id.feelsLikeTextView)
//            val tempMinTextView = findViewById<TextView>(R.id.tempMinTextView)
//            val tempMaxTextView = findViewById<TextView>(R.id.tempMaxTextView)
//            val pressureTextView = findViewById<TextView>(R.id.pressureTextView)
            val humidityTextView = findViewById<TextView>(R.id.humidityTextView)
//            val speedTextView = findViewById<TextView>(R.id.speedTextView)

            // Set the values to the TextView elements
            tempTextView.text = "Temp: ${main.getDouble("temp")}"
//            feelsLikeTextView.text = "Feels Like: ${main.getDouble("feels_like")}"
//            tempMinTextView.text = "Temp Min: ${main.getDouble("temp_min")}"
//            tempMaxTextView.text = "Temp Max: ${main.getDouble("temp_max")}"
//            pressureTextView.text = "Pressure: ${main.getInt("pressure")}"
            humidityTextView.text = "Humidity: ${main.getInt("humidity")}"
//            speedTextView.text = "Speed: ${wind.getDouble("speed")}"
        }
    }

    override fun onResume() {
        super.onResume()

        updateThemeColor()

    }

    fun updateThemeColor () {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lightModeEnabled = sharedPreferences.getBoolean("lightModeEnabled", false)

        val containerLayout = findViewById<LinearLayout>(R.id.forecast)

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