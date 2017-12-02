package com.example.hanah.a101bandouro

import android.app.ActivityManager
import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.hanah.a101bandouro.databinding.ActivityListBinding
import com.example.hanah.a101bandouro.model.MemoryItem
import com.nifty.cloud.mb.core.FetchFileCallback
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by hanah on 2017/11/12.
 */
class ListActivity: AppCompatActivity() {

    lateinit var binding: ActivityListBinding
    private val list = mutableListOf<MemoryItem>()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        Log.d("hoge","hoge")

        /*supportFragmentManager
                .beginTransaction()
                .add(R.id.list, ItemFragment.newInstance())
                .commit()*/

        list.add(MemoryItem(0,"hogehoge"))

        var mediaPlayer = MediaPlayer()
        val listAdapter = ItemListAdapter(this, list, { posision ->
            val file = if (posision > 5) {
                NCMBFile("さんぽ.mp3")
            } else {
                NCMBFile(posision.toString() + ".mp3")
            }
            file.fetchInBackground({ bytes: ByteArray?, ncmbException: NCMBException? ->

                if (!mediaPlayer.isPlaying) {
                    val tempMp3 = File.createTempFile(posision.toString() + "hogehogehoge", ".mp3", cacheDir)
                    tempMp3.deleteOnExit()
                    val fos = FileOutputStream(tempMp3)
                    fos.write(bytes)
                    fos.close()
                    mediaPlayer = MediaPlayer()

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