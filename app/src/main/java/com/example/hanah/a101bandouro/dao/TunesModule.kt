package com.example.hanah.a101bandouro.dao

import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

/**
 * 🍣 Created by hanah on 2017/12/17.
 */

class TunesModule(context: Context, val callback: Callback) {

    @Singleton
    val orma = OrmaDatabase.builder(context).name("tunes.db").build()

    private fun insert(tunesName: String) {

        val tune = Tunes().apply { tunes = tunesName }

        /*insert実行*/
        Single.fromCallable {
            orma.transactionSync {
                orma.insertIntoTunes(tune)
                orma.prepareInsertIntoTunes().execute(tune)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun searchThenInsert(tunesName: String) {
        orma.selectFromTunes()
            .tunesEq(tunesName)
            .executeAsObservable()
            .toList()
            .subscribe({
                //同じものがdbに存在しなかったらinsert
                if (it.isEmpty()) {
                    insert(tunesName)
                }
            }, {
                it.printStackTrace()
            })
    }

    fun read() {
        orma.selectFromTunes()
            .orderByTunesDesc()
            .executeAsObservable()
            .toList()
            .subscribe({
                callback.tunesList(it)
            }, {
                it.printStackTrace()
                callback.error()
            })
    }

    //この手で全て消し去る(delete all)
    fun delete() {
        orma.deleteFromTunes()
            .execute()
    }


    interface Callback {
        fun tunesList(tunesList: MutableList<Tunes>)
        fun error()
    }
}