package com.example.hanah.a101bandouro.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.hanah.a101bandouro.R
import com.example.hanah.a101bandouro.view.MainFragment
import javax.security.auth.callback.Callback

/**
 * 🍣 Created by hanah on 2017/12/17.
 */
class MainViewModel(callback: Callback, private val fragment: MainFragment) : BaseObservable() {

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

    }

    fun onClickTasteDownButton(view: View) {

    }

    fun onClickMoveToMemoryListButton(view: View) {

    }

}