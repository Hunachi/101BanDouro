package com.example.hanah.a101bandouro.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.hanah.a101bandouro.R
import com.example.hanah.a101bandouro.view.ListActivity
import com.example.hanah.a101bandouro.view.MainFragment
import javax.security.auth.callback.Callback

/**
 * 🍣 Created by hanah on 2017/12/17.
 */
class MainViewModel(val context: Context, private val fragment: MainFragment, callback: Callback)
    : BaseObservable(), MainFragment.Callback {

    private val tastesCount = 3 //一つの駅に対する曲の数（今のところ固定）

    @BindingAdapter("changeImage")
    fun changeImage(view: ImageView, playingMusic: Boolean) {
        if (playingMusic) {
            Glide.with(view).load(R.drawable.porse_play).into(view)
        } else {
            Glide.with(view).load(R.drawable.start_play).into(view)
        }
    }

    var count = 1

    @Bindable
    var playingMusic = false

    @Bindable
    var stationName = ""

    @Bindable
    var counterText = ""
        get() = count.toString()

    @Bindable
    var detailText = ""

    fun onClickMusicStartButton(view: View) {
        playingMusic = if (!playingMusic) {
            //music start
            //todo locationStart()
            fragment.run {
                stopMusic()
                //todo getNearStation(pointX = point.first, pointY = point.second, tasteful = count)
            }
            false
        } else {
            //music stop
            //todo locationManager.removeUpdates(this)
            fragment.stopMusic()
            true
        }
    }

    fun onClickTasteUpButton(view: View) {
        count.plusCount
        if (playingMusic) playMusic()
    }

    fun onClickTasteDownButton(view: View) {
        count.minusCount
        if (playingMusic) playMusic()
    }

    fun onClickMoveToMemoryListButton(view: View) {
        val intent = Intent(context, ListActivity::class.java)
        startActivity(context, intent, null)
    }

    //渋さの変更
    private fun playMusic() {
        fragment.run {
            stopMusic()
            changeTasteful(count)
        }
    }

    override fun setText(station: String, tuneTitle: String) {
        stationName = station.plus("付近")
        detailText = tuneTitle
    }

    /*拡張関数*/
    private var Int.plusCount: Int
        get() =
            if (this < tastesCount) this + 1 else 1
        set(value) {
            counterText = value.toString()
        }

    private var Int.minusCount: Int
        get() = if (this > 0) this - 1 else tastesCount
        set(value) {
            counterText = value.toString()
        }

    interface Callback{

    }

}