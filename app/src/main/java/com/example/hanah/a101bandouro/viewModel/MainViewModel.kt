package com.example.hanah.a101bandouro.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.example.hanah.a101bandouro.BR
import javax.security.auth.callback.Callback

/**
 * 🍣 Created by hanah on 2017/12/17.
 */
class MainViewModel(callback: Callback) : BaseObservable() {

    var count = 1
    var playMusic = false

    @Bindable
    var stationName = ""

    @Bindable
    var counterText = ""
        get() = count.toString()

    @Bindable
    var detailText = ""

    fun onClickMusicStartButton(view: View) {
        if(playMusic){

        }else{

        }
    }

    fun onClickTasteUpButton(view: View) {

    }

    fun onClickTasteDownButton(view: View) {

    }

    fun onClickMoveToMemoryListButton(view: View){

    }

}