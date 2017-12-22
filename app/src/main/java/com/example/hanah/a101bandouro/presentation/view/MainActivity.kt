package com.example.hanah.a101bandouro.presentation.view


import android.os.Bundle
import com.nifty.cloud.mb.core.NCMB
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import com.example.hanah.a101bandouro.R
import com.example.hanah.a101bandouro.useCase.dao.Tunes
import com.example.hanah.a101bandouro.useCase.provider.TunesModule
import com.example.hanah.a101bandouro.databinding.ActivityMainBinding
import com.example.hanah.a101bandouro.useCase.model.Key
import com.example.hanah.a101bandouro.useCase.provider.LocationProvider
import com.example.hanah.a101bandouro.presentation.viewModel.MainViewModel

class MainActivity : AppCompatActivity(), MainViewModel.Callback, TunesModule.Callback {

    private lateinit var presenter: MainPresenter
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*ニフクラ ファイルストレージ first initialize*/
        NCMB.initialize(this, Key.nifty.first, Key.nifty.second)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val viewModel = MainViewModel(this, this)
        presenter = MainPresenter(this, viewModel)
        binding.viewModel = viewModel

        /*start location*/
        val locationProvider = LocationProvider(this, viewModel)
        locationProvider.onCreate()

    }

    override fun error() {}

    override fun tunesList(tunesList: MutableList<Tunes>) {}

    override fun getFragmentInstance(): MainPresenter = presenter

}