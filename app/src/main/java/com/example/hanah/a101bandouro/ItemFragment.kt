package com.example.hanah.a101bandouro

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hanah.a101bandouro.databinding.FragmentItemListBinding
import com.nifty.cloud.mb.core.FetchFileCallback
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ItemFragment : Fragment() {
    private lateinit var listAdapter: ItemListAdapter
    private var list = mutableListOf<String>()
    private lateinit var binding: FragmentItemListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentItemListBinding.inflate(inflater, container, false)
        setList()
        var mediaPlayer = MediaPlayer()
        listAdapter = ItemListAdapter(context, list, { posision ->
            val file = if (posision > 5) {
                NCMBFile("さんぽ.mp3")
            } else {
                NCMBFile(posision.toString() + ".mp3")
            }
            file.fetchInBackground(FetchFileCallback() { bytes: ByteArray?, ncmbException: NCMBException? ->

                if (!mediaPlayer.isPlaying) {
                    val tempMp3 = File.createTempFile(posision.toString() + "hogehogehoge", ".mp3", context.cacheDir)
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
        return binding.root
    }

    private fun setList() {
        list.add("秋葉原")
        list.add("原宿")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance(): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
