package com.example.hanah.a101bandouro

import android.os.Bundle
import android.util.Log
import com.example.hanah.a101bandouro.client.ServerClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.sql.ResultSet

/**
 * Created by hanah on 2017/11/11.
 */
class MainFragment: android.support.v4.app.Fragment() {
    var activity_: MainActivity? = null
    companion object {

    }
    fun newInstance(pointX: Double, pointY: Double, nowStationName: String): MainFragment{
        val fragment = MainFragment()
        val args = Bundle()
        args.putDouble("x", pointX)
        args.putDouble("y", pointY)
        args.putString("station", nowStationName)
        Log.d(pointX.toString()+" : "+ pointY.toString(), nowStationName)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("hoge", "hoge")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity_ = activity as MainActivity?
    }

    fun getNearStation(){
        var newStation: String
        val client = ServerClient("KEY")
        client
                .findStation(arguments.getDouble("x",
                        33.589085), arguments.getDouble("y",130.398821))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    newStation = it.ResultSet.Point.Station.Name
                    Log.d("近くの駅",newStation)
                },{
                    it.printStackTrace()
                })
    }



    fun getData(){

    }
}