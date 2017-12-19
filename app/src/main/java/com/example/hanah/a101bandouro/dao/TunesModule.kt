package com.example.hanah.a101bandouro.dao

import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import org.antlr.v4.runtime.atn.SemanticContext
import javax.inject.Singleton

/**
 * 🍣 Created by hanah on 2017/12/17.
 */

class TunesModule(context: Context, val callback: Callback) {

    private var searching = false

    @Singleton
    val orma = OrmaDatabase.builder(context).name("tunes.db").build()

    private fun insert(tunesName: String) {

        val tune = Tunes().apply { tunes = tunesName }

        /*insert実行*/
        Single.fromCallable {
            orma.transactionSync {
                orma.insertIntoTunes(tune)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { searching = false },
                {
                    it.printStackTrace()
                    searching = false
                }
            )
    }

    fun searchThenInsert(tunesName: String) {
        if (searching) return
        searching = true
        /*同じものがdbにあるかconfirm*/
        orma.selectFromTunes()
            .tunesEq(tunesName)
            .executeAsObservable()
            .toList()
            .subscribe({
                Log.d("はお", it.size.toString())
                if (it.size == 0) insert(tunesName)
            }, {
                searching = false
                it.printStackTrace()
            })
    }

    fun read() {
        if (searching) return
        searching = true
        orma.selectFromTunes()
            .orderByTunesDesc()
            .executeAsObservable()
            .toList()
            .subscribe({
                callback.tunesList(it)
                searching = false
            }, {
                it.printStackTrace()
                searching = false
                callback.error()
            })
    }

    //この手で全て消し去る(delete all)
    fun delete() {
        Single.fromCallable { orma.deleteFromTunes().execute() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    interface Callback {
        fun tunesList(tunesList: MutableList<Tunes>)
        fun error()
    }
}