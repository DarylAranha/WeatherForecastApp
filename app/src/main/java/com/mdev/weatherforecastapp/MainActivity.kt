package com.mdev.weatherforecastapp

import WeatherResponse
import WeatherService
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TextView
import android.widget.TableRow
import android.view.ViewGroup.LayoutParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import android.location.Location
import android.location.LocationListener
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val API_KEY = "cbaf267cfada9b18d0e0c37a8801d930"
    private lateinit var retrofit: Retrofit
    private lateinit var weatherService: WeatherService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherService = retrofit.create(WeatherService::class.java)

        // Check if the location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, start listening for location updates

            val locationArray = arrayOf(
                Pair(51.5072, -0.1276),
                Pair(48.8566, 2.3522),
                Pair(40.7128, -74.0060)
            )

            for (location in locationArray) {
                getWeatherData(location.first, location.second, false)
            }

            startLocationUpdates()
        }

        val settingButton = findViewById<Button>(R.id.settings)
        settingButton.setOnClickListener {
            // Call the function to navigate to the next activity
            onSettingsButtonClick()
        }

        val forecastButton = findViewById<Button>(R.id.forecast)
        forecastButton.setOnClickListener {
            // Call the function to navigate to the next activity
            onForecastButtonClick()
        }
    }

    override fun onResume() {
        super.onResume()

        updateThemeColor()
        showNotification()

    }

    fun updateThemeColor () {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lightModeEnabled = sharedPreferences.getBoolean("lightModeEnabled", false)

        val containerLayout = findViewById<LinearLayout>(R.id.activit_layout)

        if (lightModeEnabled) {
            containerLayout.setBackgroundColor(Color.BLACK)
            changeTextViewsColor(findViewById(android.R.id.content), Color.WHITE)
        } else {
            containerLayout.setBackgroundColor(Color.WHITE)
            changeTextViewsColor(findViewById(android.R.id.content), Color.BLACK)
        }
    }

    fun showNotification() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val notificationEnabled = sharedPreferences.getBoolean("notificationEnabled", false)
        val temp = sharedPreferences.getString("temperature", null)
        if (notificationEnabled && (temp != null)) {
            showTemperatureUpdateNotification(temp)
        }
    }

//    private fun showTemperatureUpdateNotification(temp: String) {
//        // Create a NotificationManager
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Create a notification channel for Android 8.0 (Oreo) and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "TemperatureUpdateChannel"
//            val channelName = "Temperature Update Channel"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(channelId, channelName, importance)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Create a notification builder
//        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            "TemperatureUpdateChannel"
//        } else {
//            // For devices below Android 8.0, you can use an empty string
//            ""
//        }
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Temperature Update")
//            .setContentText("The current temperature is $temp °C")
//            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace this with your notification icon
//
//        // Show the notification
//        notificationManager.notify(0, notificationBuilder.build())
//
//        // Create an intent for the notification
//        val notificationIntent = Intent(this, NotificationReceiver::class.java)
//        notificationIntent.putExtra("temperature", temp) // Pass temperature data if needed
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // Set the interval for the alarm (5 minutes in milliseconds)
//        val intervalMillis: Long = 15 * 1000
//
//        // Get the AlarmManager service
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        // Set a repeating alarm that triggers the notification every 5 minutes
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis() + intervalMillis,
//            intervalMillis,
//            pendingIntent
//        )
//    }

    private fun showTemperatureUpdateNotification(temp: String) {
        // Create a NotificationManager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "TemperatureUpdateChannel"
            val channelName = "Temperature Update Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a notification builder
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            "TemperatureUpdateChannel"
        } else {
            // For devices below Android 8.0, you can use an empty string
            ""
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Temperature Update")
            .setContentText("The current temperature is $temp °C")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace this with your notification icon

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build())

        // Create an intent for the notification
        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        notificationIntent.putExtra("temperature", temp) // Pass temperature data if needed

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the interval for the alarm (15 seconds in milliseconds)
        val intervalMillis: Long = 15 * 1000

        // Get the AlarmManager service
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set a repeating alarm that triggers the notification every 15 seconds
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
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

    fun onSettingsButtonClick() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun onForecastButtonClick() {
        val intent = Intent(this, Forecast::class.java)
        startActivity(intent)
    }

    private fun startLocationUpdates() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Do something with latitude and longitude here
                // For example, print them to the console
                println("Latitude: $latitude, Longitude: $longitude")

                getWeatherData(latitude, longitude, true)

                // Don't forget to stop listening for location updates if you no longer need them
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        }
    }

    private fun createCityTableRow(city: String, temp: Double): TableRow {
        val row = TableRow(this)
        val params = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        row.layoutParams = params

        val cityTextView = TextView(this)
        cityTextView.text = "$city ="
        cityTextView.setTypeface(null, Typeface.BOLD)
        row.addView(cityTextView)

        val tempTextView = TextView(this)
        tempTextView.text = "${temp.toString()} °C"
        row.addView(tempTextView)

        return row
    }

    private fun updateMainView(temperature: Double) {
        val temp = findViewById<TextView>(R.id.temp);

        temp.text = "$temperature";
    }

    private fun updateCityTempViews(city: String, temp: Double) {
        val cityTempTable = findViewById<TableLayout>(R.id.cityTempTable)

        // Create a new row for the city and add it to the TableLayout
        val cityTableRow = createCityTableRow(city, temp)
        cityTempTable.addView(cityTableRow)
    }

    private fun getWeatherData(latitude: Double, longitude: Double, updateMain: Boolean = false) {
        val call = weatherService.getWeatherData(latitude, longitude, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val lat = it.coord.lat
                        val lon = it.coord.lon
                        println("Weather API Response: Latitude: $lat, Longitude: $lon")

                        val temp = it.main.temp // Get the temperature from the API response
                        val city = it.name // Get the city name from the API response

                        // Update the UI with the weather data
                        if (updateMain) {
                            updateCityTempViews(city, temp)
                        } else {
                            updateMainView(temp)

                            val jsonResponse = Gson().toJson(it)
                            saveWeatherResponseToSharedPreferences(jsonResponse)

                            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val notificationEnabled = sharedPreferences.getBoolean("notificationEnabled", false)
                            if (notificationEnabled) {
                                showTemperatureUpdateNotification(temp.toString())
                            }

                            val editor = sharedPreferences.edit()
                            editor.putString("temperature", temp.toString())
                            editor.apply()
                        }
                    }
                } else {
                    println("Weather API Call failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                println("Weather API Call failed: ${t.message}")
            }
        })
    }

    private fun saveWeatherResponseToSharedPreferences(weatherResponse: String) {
        Log.d("DATA", weatherResponse)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("weatherResponse", weatherResponse)
        editor.apply()
    }

    // Handle the result of the location permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start listening for location updates
                startLocationUpdates()
            } else {
                // Permission denied, handle accordingly (show a message, etc.)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}