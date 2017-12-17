package com.example.hanah.a101bandouro.dao

import android.content.Context
import javax.inject.Singleton

/**
 * 🍣 Created by hanah on 2017/12/17.
 */

class TunesModule(context: Context, val callback: Callback) {


    var hasCreated: Boolean = false

    @Singleton
    val orma = OrmaDatabase.builder(context).name("tunes.db").build()


    fun insert(tunesName: String) {
        val tune = Tunes()
        tune.apply {
            tunes = tunesName
        }
        orma.insertIntoTunes(tune)

        //insert実行
        orma.transactionSync {
            orma.prepareInsertIntoTunes().execute(tune)
        }
    }

    fun read(){
        orma.selectFromTunes()
            .executeAsObservable()
            .toList()
            .subscribe({
                //callback.tunesList()
            },{
                it.printStackTrace()
                //callback.error()
            })
    }

    //この手で全て消し去る(delete all)
    fun delete(){
        orma.deleteFromTunes()
            .execute()
    }


    interface Callback {

    }
}