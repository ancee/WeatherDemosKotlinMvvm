package com.weatherdemo.currentweather.network

import com.weatherdemo.currentweather.model.WeatherModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApiServices {

    /**
     * Companion object to create the NetworkApiServices
     */
    @GET("data/2.5/weather?")
    fun getWeather(@Query("q") city: String, @Query("APPID") app_id: String): Call<WeatherModel>


    companion object Factory {
        var BASE_URL = "https://api.openweathermap.org/"



        fun create(): NetworkApiServices {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(NetworkApiServices::class.java);
        }
    }
}