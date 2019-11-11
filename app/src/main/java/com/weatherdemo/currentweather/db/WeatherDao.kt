package com.weatherdemo.currentweather.db

import androidx.room.*
import com.weatherdemo.currentweather.model.WeatherModel


@Dao
public interface WeatherDao  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOnlySingleRecord(model: WeatherModel)

    @Query("SELECT * FROM weather_table WHERE city =:city")
    fun getSingleRecord(city: String): WeatherModel

    @Update
    fun updateRecord(model: WeatherModel)
}