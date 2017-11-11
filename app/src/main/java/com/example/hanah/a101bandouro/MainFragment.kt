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
        file.fetchInBackground(FetchFileCallback() { bytes: ByteArray, ncmbException: NCMBException? ->
            audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    22050,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bytes.size,
                    AudioTrack.MODE_STREAM
            )
        })
        Log.d("hoge", "hoge")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun getNearStation(pointX: Double, pointY: Double) {
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
                        getData(station = newStation)
                    }
                }, {
                    it.printStackTrace()
                })
    }

    fun getData(station: String) {
        val file = NCMBFile(station + "2" + ".wav")
        file.fetchInBackground(FetchFileCallback() { bytes: ByteArray?, ncmbException: NCMBException? ->

            /*if (bytes != null) {
                audioTrack = AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        22050,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bytes.size,
                        AudioTrack.MODE_STREAM
                )
                audioTrack?.write(bytes, 0, bytes.size)
                if (audioTrack != null) audioTrack?.play()
            }

*/
            /*val tempMp3 = File.createTempFile(station, ".mp3", contexts.cacheDir)
            tempMp3.deleteOnExit()
            FileOutputStream(tempMp3).run {
                write(bytes)
                close()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(
                    FileInputStream(tempMp3).fd
            )
            mediaPlayer.prepare()
            mediaPlayer.start()*/

        })
    }

    fun changeTasteful(tasteful: Int) {

    }
}