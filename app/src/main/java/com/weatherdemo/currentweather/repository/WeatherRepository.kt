package com.weatherdemo.currentweather.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.weatherdemo.currentweather.db.WeatherDataBase
import com.weatherdemo.currentweather.model.WeatherModel
import com.weatherdemo.currentweather.network.NetworkApiServices
import com.weatherdemo.currentweather.receiver.MyAlarmReceiver
import com.weatherdemo.currentweather.view.MainActivity
import com.weatherdemo.currentweather.view.MainActivity.Companion.PREF_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback


class WeatherRepository(val application: Application) {

    fun requestForWeatherData(
        weatherModelObserver: MutableLiveData<WeatherModel>?,
        city: String,
        isFromApi: Boolean
    ) {

        val dbInstance =
            Room.databaseBuilder(application, WeatherDataBase::class.java, MainActivity.DB_NAME).build()



        if (isFromApi) {
            val call = NetworkApiServices.create().getWeather(city, AppId)


            call.enqueue(object : Callback, retrofit2.Callback<WeatherModel> {
                override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                    if (response.code() == 200) {
                        val sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                        val currentDateTime = Date(System.currentTimeMillis())
                        val weatherResponse = response.body()
                        weatherResponse?.city = city

                        // DatabaseAsync().execute()


                        /*        weatherResponse?.let {
                                    dbInstance.daoAccess().insertOnlySingleRecord(it)
                                }*/
                        GlobalScope.launch {
                            weatherResponse?.let {
                                dbInstance.daoAccess().insertOnlySingleRecord(it)
                            }
                        }
                        weatherModelObserver?.let {
                            it.value = (weatherResponse)
                        }
                        val stringBuilder = /*"Country: " +
                                weatherResponse?.sys?.country +
                                "\n" +*/
                            "Temperature: " +
                                    weatherResponse?.main?.temp +
                                    "\n" +
                                    "Temperature(Min): " +
                                    weatherResponse?.main?.temp_min +
                                    "\n" +
                                    "Temperature(Max): " +
                                    weatherResponse?.main?.temp_max +
                                    "\n" +
                                    "Humidity: " +
                                    weatherResponse?.main?.humidity +
                                    "\n" +
                                    "Pressure: " +
                                    weatherResponse?.main?.pressure

                        Log.i("data", "" + weatherResponse)
                        Log.i("data", "string builder- " + stringBuilder)

                        //Log.e("v","date first :"+ date.time)
                        sharedPreferences?.edit()?.putLong(MainActivity.PREF_KEY_CALL_API, currentDateTime.time)
                            ?.apply()


                        var receiver = MyAlarmReceiver()
                        // 2 hour alarm
                        receiver.setSecondAlarm(application,2*60*60)
                        /*
                                val editor = sharedPreferences.edit()
                                editor.putString(PREF_KEY, stringBuilder.toString())
                                editor.apply()*/

                        // weatherData?.text = stringBuilder
                    } else {
                        Log.i("data", "error- " + response.code());
                    }
                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    // weatherData?.text = t.message
                    Log.i("data", "error- " + t.message);
                }
            })
        } else {
            /*   doAsync {
                   var weatherResponse = dbInstance.daoAccess().getSingleRecord(city)
                   weatherModelObserver?.let {
                       it.value = (weatherResponse)
                   }
               }*/

            GlobalScope.launch {
                var weatherResponse = dbInstance.daoAccess().getSingleRecord(city)
                weatherModelObserver?.let {

                    withContext(Dispatchers.Main) {
                        it.value = (weatherResponse)
                    }

                }
            }
            /*  *//*

            }*/

        }

    }

    companion object {

        var AppId = "add your app id"

    }


}
