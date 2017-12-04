package com.example.hanah.a101bandouro

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.hanah.a101bandouro.Adapter.ItemListAdapter
import com.example.hanah.a101bandouro.databinding.ActivityListBinding
import com.example.hanah.a101bandouro.model.MemoryItem
import com.example.hanah.a101bandouro.tool.DatabaseModel
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBFile
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by hanah on 2017/11/12.
 */
class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        setTunesList()

    }

    private fun setTunesList() {
        val list = mutableListOf<String>()
        Single.fromCallable {
            list.addAll(DatabaseModel(this).getTunes())
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setListAdapter(list)
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, "曲の情報がありません", Toast.LENGTH_SHORT).show()
                })
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
                    val fos = FileOutputStream(tempMp3)
                    fos.write(bytes)
                    fos.close()
                    //mediaPlayer = MediaPlayer()

                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(
                            FileInputStream(tempMp3).fd
                    )
                    //mediaPlayer.isLooping = true
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
            })
        })
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        listAdapter.notifyItemMoved(0, list.size)
    }

}
