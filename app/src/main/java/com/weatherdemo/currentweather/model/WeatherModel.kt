package com.weatherdemo.currentweather.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather_table")
data class WeatherModel(


    var city: String? = null
) {

/*    @Ignore
    @SerializedName("coord")
    var coord: CoordModel? = null

    @Embedded(prefix = "sys")
    @SerializedName("sys")
    var sys: SysModel? = null

    @Ignore
    @SerializedName("weather")
    var weather = ArrayList<Weather>()*/

    @Embedded(prefix = "main")
    @SerializedName("main")
    var main: MainModel? = null
/*
    @Ignore
    @SerializedName("wind")
    var wind: WindModel? = null

    @Ignore
    @SerializedName("rain")
    var rain: RainModel? = null

    @Ignore
    @SerializedName("clouds")
    var clouds: CloudsModel? = null*/

    @Ignore
    @SerializedName("dt")
    var dt: Float = 0.toFloat()

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int? = 0

    @Ignore
    @SerializedName("name")
    var name: String? = null

    @Ignore
    @SerializedName("cod")
    var cod: Float? = 0.toFloat()


/*    data class Weather(val v: String) {
        @SerializedName("id")
        var id: Int = 0
        @SerializedName("main")
        var main: String? = null
        @SerializedName("description")
        var description: String? = null
        @SerializedName("icon")
        var icon: String? = null
    }*/

/*   data class CloudsModel(val v: String) {
       @SerializedName("all")
       var all: Float = 0.toFloat()
   }

   data class RainModel(val v: String) {
       @SerializedName("3h")
       var h3: Float = 0.toFloat()
   }

   data class WindModel(val v: String) {
       @SerializedName("speed")
       var speed: Float = 0.toFloat()
       @SerializedName("deg")
       var deg: Float = 0.toFloat()
   }*/

    data class MainModel(
        @SerializedName("temp")
        var temp: Float? = null,
        @SerializedName("humidity")
        var humidity: Float? = null,
        @SerializedName("pressure")
        var pressure: Float? = null,
        @SerializedName("temp_min")
        var temp_min: Float? = null,
        @SerializedName("temp_max")
        var temp_max: Float? = null
    )


/*  data class SysModel(val v: String) {
      @SerializedName("country")
      var country: String? = null
      @Ignore
      @SerializedName("sunrise")
      var sunrise: Long = 0
      @Ignore
      @SerializedName("sunset")
      var sunset: Long = 0
  }

  data class CoordModel(val v: String) {
      @SerializedName("lon")
      var lon: Float = 0.toFloat()
      @SerializedName("lat")
      var lat: Float = 0.toFloat()
  }*/
}