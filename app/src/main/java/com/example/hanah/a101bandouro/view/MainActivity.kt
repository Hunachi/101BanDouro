package com.example.hanah.a101bandouro.view

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import android.Manifest
import android.content.Context

import com.nifty.cloud.mb.core.NCMB
import android.databinding.DataBindingUtil
import com.example.hanah.a101bandouro.R
import com.example.hanah.a101bandouro.dao.Tunes
import com.example.hanah.a101bandouro.dao.TunesModule
import com.example.hanah.a101bandouro.databinding.ActivityMainBinding
import com.example.hanah.a101bandouro.model.Key
import com.example.hanah.a101bandouro.viewModel.MainViewModel

class MainActivity : AppCompatActivity(), MainViewModel.Callback, TunesModule.Callback {

    private lateinit var fragment: MainFragment
    lateinit var binding: ActivityMainBinding
    private val requestCodeLocation = 278

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //ニフクラ ファイルストレージ first initialize
        NCMB.initialize(this, Key.nifty.first, Key.nifty.second)

        fragment = MainFragment(this)
        binding.viewModel = MainViewModel(this, fragment, this)

        /*todo checkPermission()*/

    }

    /*// Permissionの結果の受け取り
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == requestCodeLocation) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart()
                return
            } else {
                Toast.makeText(this, "許可がないとアプリを利用できません", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

    override fun error() {}

    override fun tunesList(tunesList: MutableList<Tunes>) {}

}