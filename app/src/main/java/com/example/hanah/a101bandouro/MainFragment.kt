package com.example.hanah.a101bandouro

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.hanah.a101bandouro.client.ServerClient
import com.nifty.cloud.mb.core.FetchFileCallback
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.os.Environment.getExternalStorageDirectory
import java.io.IOException
import android.R.raw
import android.content.Context
import io.reactivex.Single


@SuppressLint("ValidFragment")
/**
 * Created by hanah on 2017/11/11.
 */
class MainFragment(val callback: Callback, context: Context) : android.support.v4.app.Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private var station = ""
    private var audioTrack: AudioTrack? = null
    private var contexts = context

    interface Callback {
        fun setText(station: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer = MediaPlayer()
        val file = NCMBFile(station + "2" + ".wav")
        Log.d("hoge", "hoge")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun getNearStation(pointX: Double, pointY: Double, tasteful: Int) {
        var newStation: String
        val client = ServerClient("eBBWPyXMYduCN759")
        client
                .findStation(pointX, pointY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    newStation = it.ResultSet.Point.Station.Name
                    callback.setText(newStation)
                    Log.d("近くの駅", newStation)
                    if (station != newStation) {
                        station = newStation
                        getData(station = newStation, tasteful = tasteful)
                    }else{
                        getData("", tasteful)
                    }
                }, {
                    getData("", tasteful)
                    it.printStackTrace()
                })
    }

    fun getData(station: String, tasteful: Int) {
        val file =if(station.isBlank()){
            NCMBFile( "さんぽ.mp3")
        }else{
            NCMBFile(station + tasteful.toString() + ".mp3")
        }
        file.fetchInBackground(FetchFileCallback() { bytes: ByteArray?, ncmbException: NCMBException? ->

            val tempMp3 = File.createTempFile(station + "hogehoge", ".mp3", contexts.cacheDir)
            tempMp3.deleteOnExit()
            val fos = FileOutputStream(tempMp3)
            fos.write(bytes)
            fos.close()
            mediaPlayer = MediaPlayer()

            mediaPlayer.reset()
            mediaPlayer.setDataSource(
                    FileInputStream(tempMp3).fd
            )
            mediaPlayer.isLooping = true
            mediaPlayer.prepare()
            mediaPlayer.start()

        })
    }

    fun changeTasteful(tasteful: Int) {

    }
}