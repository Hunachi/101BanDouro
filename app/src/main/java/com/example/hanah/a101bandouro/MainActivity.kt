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
import android.widget.ImageButton


class MainActivity : AppCompatActivity(), LocationListener, MainFragment.Callback {
    private lateinit var mediaPlayer: MediaPlayer
    private var locationManager: LocationManager? = null
    private var fragment: MainFragment? = null
    private var textView1: TextView? = null
    private var textView2: TextView? = null
    private lateinit var stationText: TextView
    private lateinit var detailText: TextView
    private var count = 2
    private var playable = true
    private var musicSize = 3
    private var nowStation = ""
    private var point = Pair(0.0, 0.0)
    private var detaillist: MutableList<String> = mutableListOf()

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView1 = findViewById(R.id.text_view1)
        textView2 = findViewById(R.id.text_view2)
        val button1: ImageButton = findViewById(R.id.up_button)
        val button2: ImageButton = findViewById(R.id.down_button)
        val textView3 = findViewById<View>(R.id.textView) as TextView

        //first setting
        NCMB.initialize(
                this,
                "4bf00452b48657802fb11f962c23564281cf5abd3ef0bb2483104378dda55441",
                "4b020547b0b0803b9136c1a84c5c62d48659a142fa16c9a3f43a567e50b0e270"
        )

        //fragment
        fragment = MainFragment(this, this)


        //todo hoge
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }


        // 音楽開始ボタン
        val buttonStart = findViewById<ImageButton>(R.id.start)

        // リスナーをボタンに登録
        buttonStart.setOnClickListener {
            if (playable) {
                //start
                buttonStart.setImageResource(R.drawable.porse_play)
                locationStart()
            } else {
                //stop
                buttonStart.setImageResource(R.drawable.start_play)
                locationManager?.removeUpdates(this)//todo
            }
        }

        button1.setOnClickListener {
            count--
            when (count) {
                0 -> count = musicSize
            }
            textView3.setText(count)
            fragment!!.getNearStation(point.first, point.second, count)
        }

        button2.setOnClickListener {
            count++
            when (count) {
                musicSize + 1 -> count = 1
            }
            textView3.setText(count)
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
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30, 80f, this)
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
        point = Pair(location.longitude, location.latitude)
        fragment!!.getNearStation(point.first/*35.6783055555*/, point.second/*139.77044166*/, 1)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun setText(station: String) {
        stationText.text = station + " 付近"
        detailText.text = when(count){
            1 -> ""
            2 -> ""
            3 -> ""
            else -> ""
        }
    }

    private fun setTestView(){

    }

}