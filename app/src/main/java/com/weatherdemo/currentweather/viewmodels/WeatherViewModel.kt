package com.weatherdemo.currentweather.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.weatherdemo.currentweather.model.WeatherModel
import com.weatherdemo.currentweather.repository.WeatherRepository
import com.weatherdemo.currentweather.view.MainActivity
import java.util.*

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    val WeatherModelObserver: MutableLiveData<WeatherModel>? = MutableLiveData()
    private var weatherRepo: WeatherRepository? = null

    private val context = getApplication<Application>().applicationContext

    init {
        weatherRepo = WeatherRepository(application)

    }

    fun getDataFromRepo(city: String) {

        // other time
        //get date time from shared preference
        val sharedPreferences = context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE)

        val timeStamp = sharedPreferences?.getLong(MainActivity.PREF_KEY_CALL_API, 0L)
        //Log.e("v","save date:"+timeStamp)
        val saveDateTime = timeStamp?.let { Date(it) }

        //Log.e("v", "save date:" + myDate)
        val currentDateTime = Date(System.currentTimeMillis())
        // find different between current and save
        val diff = saveDateTime?.let { findDifferenceDate(currentDateTime, it) }
        //if above 2 hour get from api other wise from db
        val isAboveTwo: Boolean? = diff?.let { it >= 2 }
        isAboveTwo?.let { weatherRepo?.requestForWeatherData(WeatherModelObserver, city, it) }


    }

    private fun findDifferenceDate(currentDateTime: Date, saveDateTime: Date): Int {
        //milliseconds
        var different = currentDateTime.time - saveDateTime.time;

        // Log.e("v", "different : " + different)

        val secondsInMilli: Long = 1000
        val minutesInMilli: Long = secondsInMilli * 60
        val hoursInMilli: Long = minutesInMilli * 60
        val daysInMilli: Long = hoursInMilli * 24

        val elapsedDays: Long = different / daysInMilli;
        different = different % daysInMilli

        val elapsedHours: Long = different / hoursInMilli
        different = different % hoursInMilli

        val elapsedMinutes: Long = different / minutesInMilli
        different = different % minutesInMilli

        val elapsedSeconds: Long = different / secondsInMilli

        return elapsedHours.toInt();

    }
}