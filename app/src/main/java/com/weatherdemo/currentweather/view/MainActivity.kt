package com.weatherdemo.currentweather.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.*
import android.location.GpsStatus.GPS_EVENT_STARTED
import android.location.GpsStatus.GPS_EVENT_STOPPED
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.weatherdemo.currentweather.R
import com.weatherdemo.currentweather.db.WeatherDataBase
import com.weatherdemo.currentweather.model.WeatherModel
import com.weatherdemo.currentweather.receiver.MyAlarmReceiver
import com.weatherdemo.currentweather.util.GetGpsLocation
import com.weatherdemo.currentweather.viewmodels.WeatherViewModel
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var context: MainActivity
    private lateinit var currentWeatherTV: TextView

    private var PRIVATE_MODE = 0
    private var sharedPref: SharedPreferences? = null

    //private var willCallApi= true

    companion object {
        public var DB_NAME = "weather-db"
        public var PREF_NAME = "weather-pref"
        public var PREF_KEY = "weather-key"
        public var PREF_KEY_CALL_API = "api-time"
    }

    private var dbInstance: WeatherDataBase? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        dbInstance = Room.databaseBuilder(applicationContext, WeatherDataBase::class.java, DB_NAME).build()

        currentWeatherTV = findViewById<TextView>(R.id.weatherTV) as TextView

        // this will be helpfull for store previouse hit api.
        sharedPref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        /*    Log.e("v", "date difference:" + findCallingApiDateDifference())

            if(findCallingApiDateDifference() >= 2){
                //remove timestamp key
                sharedPref!!.edit().remove(PREF_KEY_CALL_API).apply()

                // call api again... or set again alarm for 2 hour
                //set2HourAlarm()

            }
            else{
                // fetching data from room db
            }*/

        accessFineLocationPermission()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        city = getCityFromCurrentLocation()
        observeDataFromViewModel(city)
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this.applicationContext).registerReceiver(
            weatherReceiver,
            IntentFilter(MyAlarmReceiver.ACTION_WEATHER_UPDATE)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this.applicationContext).unregisterReceiver(weatherReceiver)
    }


    public fun observeDataFromViewModel(city: String) {
        val mViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        mViewModel.getDataFromRepo(city)
        mViewModel.WeatherModelObserver?.observe(this, object : Observer<WeatherModel> {
            override fun onChanged(model: WeatherModel?) {

                val weatherResponse = model
                val stringBuilder = "City: " +
                        weatherResponse?.city +
                        "\n" +
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
                currentWeatherTV.setText(stringBuilder)

                Log.e("v", "api value: " + stringBuilder)


                // flag update for stop calling api
                // willCallApi = false

                // set alarm and passing second
                // setSecondAlarm(10)

            }

        })
    }

    @SuppressLint("MissingPermission")
    private fun locationListner() {
        var lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.addGpsStatusListener(object : GpsStatus.Listener {
            override fun onGpsStatusChanged(event: Int) {
                if (event == GPS_EVENT_STARTED) {

                    Toast.makeText(context, "Location On", Toast.LENGTH_LONG).show()

                    val location = gpsTracker?.getLocation()

                    city = getLocationFromLatLong(location)
                    observeDataFromViewModel(city)


                } else if (event == GPS_EVENT_STOPPED) {

                    Toast.makeText(context, "Location Off", Toast.LENGTH_LONG).show()

                }
            }

        })
    }

    private fun accessFineLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                112
            )
        } else {

            city = getCityFromCurrentLocation()
            observeDataFromViewModel(city)

        }
    }

    private var gpsTracker: GetGpsLocation? = null

    private fun getCityFromCurrentLocation(): String {
        try {
            locationListner()
            gpsTracker = GetGpsLocation(context)
            if (gpsTracker?.canGetLocation!!) {
                val location = gpsTracker?.getLocation()
                return getLocationFromLatLong(location)
            } else {
                //getMyLocation()
                displayPromptForEnablingGPS(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun displayPromptForEnablingGPS(activity: Activity) {

        val builder = AlertDialog.Builder(activity)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Do you want open GPS setting?"

        builder.setMessage(message)
            .setPositiveButton("OK",
                DialogInterface.OnClickListener { d, id ->
                    activity.startActivity(Intent(action))
                    d.dismiss()
                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { d, id -> d.cancel() })
        builder.create().show()
    }

    private fun getLocationFromLatLong(location: Location?): String {
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

    private var city: String = ""

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            112 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    city = getCityFromCurrentLocation()
                    observeDataFromViewModel(city)


                } else {

                    Toast.makeText(context, "Location denied", Toast.LENGTH_LONG).show()


                }
            }
        }
    }

    val weatherReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var cityGet = intent?.extras?.getString("city")?.let { it }
            cityGet?.let {
                city = it
                observeDataFromViewModel(it)
            }
        }

    }


}
