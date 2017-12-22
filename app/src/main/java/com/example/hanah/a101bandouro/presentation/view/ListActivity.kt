package com.example.hanah.a101bandouro.presentation.view

import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.hanah.a101bandouro.Adapter.ItemListAdapter
import com.example.hanah.a101bandouro.R
import com.example.hanah.a101bandouro.useCase.dao.Tunes
import com.example.hanah.a101bandouro.useCase.provider.TunesModule
import com.example.hanah.a101bandouro.databinding.ActivityListBinding
import com.example.hanah.a101bandouro.useCase.model.MemoryItem
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
    private val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)
        setTunesList()
    }

    /*if this success, next execution is tunesList*/
    private fun setTunesList() {
        TunesModule(this, this).read()
    }

    override fun tunesList(tunesList: MutableList<Tunes>) {
        val list = mutableListOf<String>()
        tunesList.forEach {
            list.add(it.tunes)
        }
        setListAdapter(list)
    }

    private fun setListAdapter(list: MutableList<String>) {

        val tuneList = mutableListOf<MemoryItem>()
        tuneList.apply { list.forEach { add(MemoryItem(it)) } }

        val listAdapter = ItemListAdapter(this, tuneList, { tuneName ->

            NCMBFile(tuneName + ".mp3")
                .fetchInBackground({ bytes: ByteArray?, _: NCMBException? ->
                    if (!mediaPlayer.isPlaying) {
                        val tempMp3 = File.createTempFile(tuneName + "hogehogehoge", ".mp3", cacheDir)
                        tempMp3.deleteOnExit()

                        FileOutputStream(tempMp3).apply {
                            write(bytes)
                            close()
                        }
                        mediaPlayer.apply {
                            pause()
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
        mediaPlayer.run {
            pause()
            reset()
        }
    }

    /*errorが起きた際に呼ばれる*/
    override fun error() {
        Toast.makeText(this, "何かしらだめだったぴよ", Toast.LENGTH_SHORT).show()
    }

}
