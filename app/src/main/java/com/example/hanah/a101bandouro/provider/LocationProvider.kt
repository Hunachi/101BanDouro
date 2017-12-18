package com.example.hanah.a101bandouro.provider

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.widget.Toast
import com.example.hanah.a101bandouro.view.MainActivity
import javax.inject.Singleton

/**
 * 🍣 Created by hanah on 2017/12/18.
 */
class LocationProvider(val context: MainActivity, val callback: Callback) {

    private lateinit var locationManager: LocationManager
    private val requestCode = 278

    @Singleton
    private fun init(context: MainActivity): LocationProvider {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationProvider(context)
    }

    private fun checkPermission() {
        val permissionStatement = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionStatement != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
        }
    }

    private fun locationStart() {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(context, settingsIntent, null)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            80f,
           object : LocationListener{
               override fun onProviderEnabled(provider: String) {
                   locationStart()
               }

               //Locationをユーザーが拒否
               override fun onProviderDisabled(provider: String) {
                   Toast.makeText(context, "許可がないとアプリを利用できません", Toast.LENGTH_SHORT).show()
                   //もう一度聞く
                   locationManager.removeUpdates(this)
                   ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeLocation)
               }

               override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                   val statusText = when (status) {
                       android.location.LocationProvider.AVAILABLE -> {
                           "位置情報の取得に成功しました"
                       }
                       android.location.LocationProvider.OUT_OF_SERVICE -> {
                           locationManager.removeUpdates(this)
                           "位置情報が取得できなくなりました。アプリを立ち上げなおしてください"
                       }
                       android.location.LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                           locationManager.removeUpdates(this)
                           "位置情報が一時的に取得できていません"
                       }
                       else -> "おしゅし"
                   }
                   Toast.makeText(context, statusText, Toast.LENGTH_SHORT).show()
               }

               override fun onLocationChanged(location: Location) {
                   Log.d("location", "最寄り駅が変更された")
                   point = Pair(location.longitude, location.latitude)
                   count = 1
                   binding.counterText.text = "$count"
                   fragment.getNearStation(pointY = point.first, pointX = point.second, tasteful = count)
               }
           }
        )
    }

    interface Callback{
        fun changeLocatio(location: Pair<Double, Double>)
    }
}