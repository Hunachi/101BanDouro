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
import android.widget.ImageButton
import com.example.hanah.a101bandouro.databinding.ActivityMainBinding
import com.example.hanah.a101bandouro.model.Key


class MainActivity : AppCompatActivity(), LocationListener, MainFragment.Callback {
    private lateinit var mediaPlayer: MediaPlayer
    private var locationManager: LocationManager? = null
    private var fragment: MainFragment? = null
    /*private var textView1: TextView? = null
    private var textView2: TextView? = null
    private lateinit var stationText: TextView
    private lateinit var detailText: TextView*/
    private var count = 2
    private var playable = true
    private var musicSize = 4
    private var nowStation = ""
    private var point = Pair(0.0, 0.0)
    private var detaillist: MutableList<String> = mutableListOf()
    private lateinit var memoryButton: ImageButton
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //first setting
        NCMB.initialize(
                this,
                Key.nifty.first,
                Key.nifty.second
        )

        //fragment
        fragment = MainFragment(this, this)


        //現在地取得permission確認
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }



        binding.start.setOnClickListener {
            if (playable) {
                //start
                binding.start.setImageResource(R.drawable.porse_play)
                //locationStart()
                point = Pair(35.6783055555, 139.77044166)
                fragment = MainFragment(this, this)
                fragment!!.getNearStation(point.first, point.second, count)
                playable = false
            } else {
                //stop
                playable = true
                binding.start.setImageResource(R.drawable.start_play)
                locationManager?.removeUpdates(this)
                fragment!!.stopMusic()
            }
        }

        binding.memoryButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListActivity::class.java))
        }

        binding.downButton.setOnClickListener {
            onCount(false)
        }

        binding.upButton.setOnClickListener {
            onCount(true)
        }
    }

    private fun onCount(up: Boolean) {
        if (up) count++ else count--
        when (count) {
            0 -> count = musicSize
            musicSize + 1 -> count = 1
        }
        if (count != 4) {
            binding.counterText.text = count.toString()
        } else {
            binding.counterText.text = "N"
        }
        point = Pair(35.6783055555, 139.77044166)
        if (!playable) {
            fragment = MainFragment(this, this)
            fragment!!.getNearStation(point.first, point.second, count)
        }
    }

    private fun locationStart() {
        Log.d("debug", "locationStart()")
        // LocationManager インスタンス生成
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            // GPSを設定するように促す
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        } else {
            Log.d("debug", "gpsEnabled")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            Log.d("debug", "checkSelfPermission false")
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 80f, this)
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true")
                locationStart()
                return
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT)
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
        point = Pair(35.6783055555, 139.77044166)
        fragment!!.getNearStation(/*point.first*/35.6783055555, /*point.second*/139.77044166, 1)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun setText(station: String) {

        if (count != 4) {
            binding.stationName.text = station + " 付近"
        } else {
            binding.stationName.text = ""
        }
        binding.detailText.text = when (count) {
            1 -> "「うまるちゃん」\n作品の舞台が八王子メイン"
            2 -> "「SPARK」\n歌手のメンバーのうちの半分が\n八王子出身"
            3 -> "「異邦人」\n歌手が八王子出身。\n八王子の近くで作成"
            else -> "「さんぽ」\n楽しく歩きましょう"
        }
    }

    private fun setTestView() {

    }

}