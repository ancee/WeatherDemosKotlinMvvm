package com.weatherdemo.currentweather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weatherdemo.currentweather.model.WeatherModel


@Database(entities = [WeatherModel::class], version = 1)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun daoAccess(): WeatherDao
}