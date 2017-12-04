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
import android.widget.TextView
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import android.Manifest
import android.content.Context
import android.media.MediaPlayer

import com.nifty.cloud.mb.core.NCMB
import android.view.View
import android.widget.Button
import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.widget.ImageButton
import com.example.hanah.a101bandouro.databinding.ActivityMainBinding
import com.example.hanah.a101bandouro.model.Key


class MainActivity : AppCompatActivity(), LocationListener, MainFragment.Callback {
    private var locationManager: LocationManager? = null
    private var fragment: MainFragment? = null
    private var count = 1
    private var playable = true
    private var musicSize = 4
    private var point = Pair(35.6783055555, 139.77044166)
    private var detaillist: MutableList<Pair<String, MutableList<String>>>
            = mutableListOf(Pair("", mutableListOf()))//ハッカソンの時間に合わなくて使ってない。
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //ニフクラ ファイルストレージ first setting
        NCMB.initialize(this, Key.nifty.first, Key.nifty.second)

        fragment = MainFragment()

        //現在地取得permission確認
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }

        //再生ボタン
        binding.start.setOnClickListener {
            playable = if (playable) {
                locationStart()
                binding.start.setImageResource(R.drawable.porse_play)
                fragment!!.run {
                    stopMusic()
                    getNearStation(pointX = point.first, pointY = point.second, tasteful = count)
                }
                false
            } else {
                //stop
                binding.start.setImageResource(R.drawable.start_play)
                locationManager?.removeUpdates(this)
                fragment!!.stopMusic()
                true
            }
        }

        //思い出リストへ
        binding.memoryButton.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        //渋さを変える
        binding.apply {
            downButton.setOnClickListener {
                onCount(false)
            }
            upButton.setOnClickListener {
                onCount(true)
            }
        }
    }

    private fun onCount(up: Boolean) {
        if (up) count++ else count--
        count = when (count) {
            0 -> musicSize
            musicSize + 1 -> 1
            else -> count
        }
        binding.counterText.text = if (count > 4) count.toString() else "N"
        if (!playable) {
            fragment!!.run {
                stopMusic()
                getNearStation(point.first, point.second, count)
            }
        }
    }

    //位置情報の取得
    private fun locationStart() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }
        Log.d("location", "changeした！")
        //locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 80f, this)
        //todo locationの更新は再生時のみする。
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart()
                return
            } else {
                // それでも拒否された時の対応←コピペコードに絶対あるコメントアウト
                val toast = Toast.makeText(this, "え～", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }


    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE -> Log.d("debug", "LocationProvider.AVAILABLE")
            LocationProvider.OUT_OF_SERVICE -> Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE")
        }
    }

    override fun onLocationChanged(location: Location) {
        point = Pair(location.latitude, location.altitude)
        count = 1
        fragment!!.getNearStation(point.first, point.second, count)
        binding.counterText.text = "$count"
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun setText(station: String) {

        if (count != 4) {
            binding.stationName.text = station + " 付近"
        } else {
            binding.stationName.text = ""
        }
        if (station == "八王子") binding.detailText.text = when (count) {
            1 -> "「うまるちゃん」\n作品の舞台が八王子メイン"
            2 -> "「SPARK」\n歌手のメンバーのうちの半分が\n八王子出身"
            3 -> "「異邦人」\n歌手が八王子出身。\n八王子の近くで作成"
            else -> "「さんぽ」\n楽しく歩きましょう"
        }
    }
}