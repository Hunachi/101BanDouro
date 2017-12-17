package com.example.hanah.a101bandouro

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.example.hanah.a101bandouro.client.Server
import com.example.hanah.a101bandouro.client.ServerClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.security.Key

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("com.example.hanah.a101bandouro", appContext.packageName)
    }

    @Test
    fun getLocationTest(){
        ServerClient(com.example.hanah.a101bandouro.model.Key.eki)
                .findStation(33.335914, 130.511629)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(it.ResultSet.apiVersion, it.ResultSet.Point.Station.Name)
                },{
                    it.printStackTrace()
                })
    }
}
