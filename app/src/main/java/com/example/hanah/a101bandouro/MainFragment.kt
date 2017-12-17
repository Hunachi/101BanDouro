package com.example.hanah.a101bandouro

import android.annotation.SuppressLint
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Bundle
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
import android.content.Context
import android.widget.Toast
import com.example.hanah.a101bandouro.dao.OrmaDatabase
import com.github.gfx.android.orma.OrmaDatabaseBuilderBase
import dagger.Provides
import io.reactivex.Single
import javax.inject.Singleton


@SuppressLint("ValidFragment")
/**
 * Created by hanah on 2017/11/11.
 */
class MainFragment(val context: Context){

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var station = ""
    /*private var contexts: Context? = null*/

    interface Callback {
        fun setText(station: String, tuneTitle: String)
    }

    fun getNearStation(pointX: Double, pointY: Double, tasteful: Int) {
        //i/*f (context == null) return*/
        Log.d(pointX.toString(), pointY.toString() + "ああーーーーーーーーーーーーー！！")
        var newStation: String
        val client = ServerClient(com.example.hanah.a101bandouro.model.Key.eki)
        client
                .findStation(pointX, pointY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    newStation = it.ResultSet.Point.Station.Name
                    (context as MainActivity).setText(newStation, "$newStation + $tasteful")
                    Log.d("近くの駅", newStation + tasteful.toString())
                    if (station != newStation) {
                        station = newStation
                        playStationMusic(station = newStation, tasteful = tasteful)
                        /*DatabaseModel(context).onTunesInsert(station)*//*.onDataSearch(station)*/
                    }
                }, {
                    it.printStackTrace()
                    Toast.makeText(context, "駅の取得に失敗", Toast.LENGTH_SHORT).show()
                })
    }

    private fun playStationMusic(station: String, tasteful: Int) {
        val file = if (station.isBlank()) {
            NCMBFile("さんぽ.mp3")
        } else {
            NCMBFile(station + tasteful.toString() + ".mp3")
        }
        file.fetchInBackground({ bytes: ByteArray?, ncmbException: NCMBException? ->
            if(bytes == null){
                (context as MainActivity).setText("さんぽ", "さんぽ")
                Log.d("error", ncmbException.toString())
                playStationMusic("", 0)
                return@fetchInBackground
            }
            val tempMp3 = File.createTempFile(station + tasteful.toString() + "hogehoge", ".mp3", context?.cacheDir)
            tempMp3.deleteOnExit()
            val fos = FileOutputStream(tempMp3)
            fos.write(bytes)
            fos.close()
            mediaPlayer.stop()
            mediaPlayer.reset()
            Single.fromCallable {
                mediaPlayer.setDataSource(
                        FileInputStream(tempMp3).fd
                )
                mediaPlayer.isLooping = true
                mediaPlayer.prepare()
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mediaPlayer.start()
                    }, {
                        it.printStackTrace()
                        Toast.makeText(context, "曲の再生に失敗\n + $ncmbException", Toast.LENGTH_SHORT).show()
                    })
        })
    }

    fun stopMusic() {
       /* if (context == null) return*/
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.reset()
        }
    }

    fun changeTasteful(tasteful: Int) {

    }
}