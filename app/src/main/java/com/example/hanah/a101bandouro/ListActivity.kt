package com.example.hanah.a101bandouro

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.hanah.a101bandouro.databinding.ActivityListBinding

/**
 * Created by hanah on 2017/11/12.
 */
class ListActivity: AppCompatActivity() {

    lateinit var binding: ActivityListBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        Log.d("ListActivity","hoge")
        supportFragmentManager
                .beginTransaction()
                .add(binding.listFragment.id, ItemFragment.newInstance())
                .commit()
    }
}