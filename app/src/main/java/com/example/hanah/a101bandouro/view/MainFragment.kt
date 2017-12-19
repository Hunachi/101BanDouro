package com.example.hanah.a101bandouro.view

import android.media.MediaPlayer
import android.util.Log
import com.example.hanah.a101bandouro.client.ServerClient
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.widget.Toast
import com.example.hanah.a101bandouro.dao.TunesModule

/**
 * Created by hanah on 2017/11/11.
 */
class MainFragment(private val context: MainActivity, val callback: Callback) {

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var station = "hoge"

    fun getNearStation(pointX: Double, pointY: Double, tasteful: Int) {
        var newStation: String
        val client = ServerClient(com.example.hanah.a101bandouro.model.Key.eki)
        client
            .findStation(pointY, pointX)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("おれがえきすぱーとや $pointX $pointY",it.toString())
                newStation = it.ResultSet.Point.Station.Name
                callback.setText(newStation, "")

                /*駅が変更した時のみ実行*/
                if (station != newStation) {
                    playStationMusic(station = newStation, tasteful = tasteful)
                    station = newStation
                }
            }, {
                /*error続きの場合はそのまま.*/
                if (station.isNotBlank() && mediaPlayer.isPlaying) {
                    station = ""
                    it.printStackTrace()
                    /*さんぽを流す*/
                    playStationMusic("", 0)
                }
            })
    }

    /*さんぽを流す場合はstationがblank.*/
    private fun playStationMusic(station: String, tasteful: Int) {
        val file = if (station.isBlank()) {
            callback.setText("", "さんぽ")
            TunesModule(context, context).searchThenInsert("さんぽ")
            NCMBFile("さんぽ.mp3")
        } else {
            NCMBFile(station + tasteful.toString() + ".mp3")
        }
        file.fetchInBackground({ bytes: ByteArray?, ncmbException: NCMBException? ->

            /*データベースに曲が存在しなかったらさんぽ*/
            if (bytes == null) {
                playStationMusic("", 0)
                return@fetchInBackground
            }
            //titleをdbに保存
            if(station.isNotBlank()){
                callback.setText("$station 付近", "$station の $tasteful　曲目")
                TunesModule(context, context).searchThenInsert("$station $tasteful")
            }
            val tempMp3 = File.createTempFile(station + tasteful.toString() + "hogehoge", ".mp3", context.cacheDir)
            tempMp3.deleteOnExit()
            FileOutputStream(tempMp3).apply {
                write(bytes)
                close()
            }
            mediaPlayer.apply {
                pause()
                reset()
                setDataSource(FileInputStream(tempMp3).fd)
                isLooping = true
                prepare()
                start()
            }
        })
    }

    fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.run {
                pause()
                reset()
            }
        }
    }

    /*渋さのみの変更*/
    fun changeTasteful(tasteful: Int) {
        playStationMusic(station, tasteful)
    }

    interface Callback {
        fun setText(station: String, tuneTitle: String)
    }

}