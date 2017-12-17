package com.example.hanah.a101bandouro

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context

import com.nifty.cloud.mb.core.NCMB
import android.databinding.DataBindingUtil
import com.example.hanah.a101bandouro.dao.Tunes
import com.example.hanah.a101bandouro.dao.TunesModule
import com.example.hanah.a101bandouro.databinding.ActivityMainBinding
import com.example.hanah.a101bandouro.model.Key

class MainActivity : AppCompatActivity(), LocationListener, MainFragment.Callback, TunesModule.Callback {

    private lateinit var locationManager: LocationManager
    private lateinit var fragment: MainFragment
    private var count = 1
    private var playable = true
    private var tastesCount = 3
    private var point = Pair(0.0, 0.0)
    private lateinit var binding: ActivityMainBinding
    private val requestCodeLocation = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //ニフクラ ファイルストレージ first initialize
        NCMB.initialize(this, Key.nifty.first, Key.nifty.second)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fragment = MainFragment(this)

        checkPermission()

        //再生ボタン
        binding.start.setOnClickListener {
            playable = if (playable) {
                //music start
                locationStart()
                binding.start.setImageResource(R.drawable.porse_play)
                fragment.run {
                    stopMusic()
                    getNearStation(pointX = point.first, pointY = point.second, tasteful = count)
                }
                false
            } else {
                //music stop
                binding.start.setImageResource(R.drawable.start_play)
                locationManager.removeUpdates(this)
                fragment.stopMusic()
                true
            }
        }

        //思い出リストへ
        binding.memoryButton.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        binding.apply {
            downButton.setOnClickListener {
                count.plusCount
                if (!playable) playMusic()
            }
            upButton.setOnClickListener {
                count.minusCount
                if (!playable) playMusic()
            }
        }
    }



    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeLocation)
        }
    }

    //渋さの変更
    private fun playMusic() {
        fragment.run {
            stopMusic()
            changeTasteful(count)
        }
    }

    //位置情報の取得
    private fun locationStart() {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 80f, this)
    }

    //Locationをユーザーが許可
    override fun onProviderEnabled(provider: String) {
        locationStart()
    }

    //Locationをユーザーが拒否
    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "許可がないとアプリを利用できません", Toast.LENGTH_SHORT).show()
        //もう一度聞く
        locationManager.removeUpdates(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeLocation)
    }

    /*// Permissionの結果の受け取り
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == requestCodeLocation) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart()
                return
            } else {
                Toast.makeText(this, "許可がないとアプリを利用できません", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        val statusText = when (status) {
            LocationProvider.AVAILABLE -> {
                "位置情報の取得に成功しました"
            }
            LocationProvider.OUT_OF_SERVICE -> {
                locationManager.removeUpdates(this)
                "位置情報が取得できなくなりました。アプリを立ち上げなおしてください"
            }
            LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                locationManager.removeUpdates(this)
                "位置情報が一時的に取得できていません"
            }
            else -> "おしゅし"
        }
        Toast.makeText(this, statusText, Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(location: Location) {
        Log.d("location", "最寄り駅が変更された")
        point = Pair(location.longitude, location.latitude)
        count = 1
        binding.counterText.text = "$count"
        fragment.getNearStation(pointY = point.first, pointX = point.second, tasteful = count)
    }

    override fun setText(station: String, tuneTitle: String) {
        binding.stationName.text = station + " 付近"
        /*
        音楽データと同様,外部dbに情報を取ってくる（情報がないため未実装）
        binding.detailText.text = ""
        }*/
    }

    override fun error() {}

    override fun tunesList(tunesList: MutableList<Tunes>) {}

    /*拡張関数*/
    private var Int.plusCount: Int
        get() =
            if (this < tastesCount) this + 1 else 1
        set(value) {
            binding.counterText.text = value.toString()
        }

    private var Int.minusCount: Int
        get() = if (this > 0) this - 1 else tastesCount
        set(value) {
            binding.counterText.text = value.toString()
        }

}