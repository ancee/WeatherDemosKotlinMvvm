package com.weatherdemo.currentweather.receiver

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.weatherdemo.currentweather.repository.WeatherRepository
import com.weatherdemo.currentweather.util.GetGpsLocation
import java.io.IOException
import java.util.*

class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // we can use notification here too
        Toast.makeText(context, "Location weather data updated", Toast.LENGTH_LONG).show()
        //
        Log.e(MyAlarmReceiver::class.java.name, "welcomr receiver with " + intent)

        var city = getCityFromCurrentLocation(context)
        var weatherRepo = WeatherRepository(context?.applicationContext as Application)
        weatherRepo.requestForWeatherData(null, city, true)

        val intent = Intent(ACTION_WEATHER_UPDATE)
        intent.putExtra("city", city)
        LocalBroadcastManager.getInstance(context?.applicationContext as Application).sendBroadcast(intent)

        // setSecondAlarm(context,10)
    }

    companion object {
        val ACTION_WEATHER_UPDATE: String = "action_weather_update"
    }

    fun setSecondAlarm(context: Context?, second: Int) {


        // val local_reg = "LOCAL_NOTIFICATION"
        // context.registerReceiver(receiver, IntentFilter(local_reg))

        val intent = Intent(context, MyAlarmReceiver::class.java)
        // intent.action = local_reg
        var id = System.currentTimeMillis().toInt()

        intent.setAction("dummy_action " + id)

        val alarmManager = context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, 1331, intent, 0)

        val calendar = Calendar.getInstance()

        calendar.add(Calendar.SECOND, second)

        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        /*   alarmManager?.setInexactRepeating(
               AlarmManager.RTC_WAKEUP,
               calendar.timeInMillis + 1000 * 60 * 60 * 2,
               (1000 * 60 * 60 * 2).toLong(),
               pendingIntent
           )*/

    }

    private fun getCityFromCurrentLocation(context: Context?): String {
        try {

            var gpsTracker = GetGpsLocation(context)
            if (gpsTracker?.canGetLocation!!) {
                val location = gpsTracker?.getLocation()
                return getLocationFromLatLong(context, location)
            } else {
                //getMyLocation()
                //displayPromptForEnablingGPS(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }


    private fun getLocationFromLatLong(context: Context?, location: Location?): String {
        try {
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                val gcd = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>
                addresses = gcd.getFromLocation(lat, lng, 1)
                var currentCity = ""
                if (addresses.size > 0) {
                    currentCity = addresses[0].locality
                    println(currentCity)
                    return currentCity
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
    }


}