package com.example.hanah.a101bandouro.view

import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.hanah.a101bandouro.Adapter.ItemListAdapter
import com.example.hanah.a101bandouro.R
import com.example.hanah.a101bandouro.dao.Tunes
import com.example.hanah.a101bandouro.dao.TunesModule
import com.example.hanah.a101bandouro.databinding.ActivityListBinding
import com.example.hanah.a101bandouro.model.MemoryItem
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by hanah on 2017/11/12.
 */
class ListActivity : AppCompatActivity(), TunesModule.Callback {

    private lateinit var binding: ActivityListBinding
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)
        setTunesList()

    }

    private fun setTunesList() {
        TunesModule(this,this)
            .read()
        //if this success, next execution is
    }

    override fun tunesList(tunesList: MutableList<Tunes>) {
        val list = mutableListOf<String>()
        tunesList.forEach {
            list.add(it.tunes)
        }
        //todo listの要素自体をstring->tunesにする
        setListAdapter(list)
    }

    private fun setListAdapter(list: MutableList<String>) {

        val tuneList = mutableListOf<MemoryItem>()
        tuneList.apply { list.forEach { add(MemoryItem(it)) } }

        val listAdapter = ItemListAdapter(this, tuneList, { tuneName ->

            val file = if (tuneName.isBlank()) {
                NCMBFile("さんぽ.mp3")
            } else {
                NCMBFile(tuneName + ".mp3")
            }
            file.fetchInBackground({ bytes: ByteArray?, _: NCMBException? ->
                if (!mediaPlayer.isPlaying) {
                    val tempMp3 = File.createTempFile(tuneName + "hogehogehoge", ".mp3", cacheDir)
                    tempMp3.deleteOnExit()

                    FileOutputStream(tempMp3).apply {
                        write(bytes)
                        close()
                    }
                    mediaPlayer.apply {
                        reset()
                        setDataSource(FileInputStream(tempMp3).fd)
                        prepare()
                        start()
                    }
                }
            })
        })

        binding.list.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(binding.list.context)
        }
        listAdapter.notifyItemMoved(0, list.size)

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.pause()
        mediaPlayer.reset()
    }

    //errorが起きた際に呼ばれる
    override fun error() {
        Toast.makeText(this, "何かしらだめだったぴよ", Toast.LENGTH_SHORT).show()
    }
}
