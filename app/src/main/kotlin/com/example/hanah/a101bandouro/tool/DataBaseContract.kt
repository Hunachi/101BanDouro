package com.example.hanah.a101bandouro.tool

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.hanah.a101bandouro.model.MemoryItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 🍣 Created by hanah on 2017/12/04.
 */
class DataBaseContract {

    companion object {
        val TABLE_NAME = "tunesList"
        val MY_TUNES = "tunes"
        val TUNES_DETAIL = "detail"
        val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ( $MY_TUNES );"
        val SQL_DELETE_ENTRIES = "DELETE TABLE $TABLE_NAME ( $MY_TUNES );"
    }

}

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_NAME = "101BanDouro.db"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DataBaseContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DataBaseContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, newVersion, oldVersion)
    }

}

class DatabaseModel(context: Context) {

    private val helper: DataBaseHelper = DataBaseHelper(context)
    private val db: SQLiteDatabase = helper.writableDatabase
    val list = mutableListOf<String>()

    fun onTunesInsert(tunesTitle: String): Boolean {
        val value = ContentValues()
        value.put(DataBaseContract.MY_TUNES, tunesTitle)
        val id = db.insert(DataBaseContract.TABLE_NAME, null, value)
        return if (id < 0) {
            Log.ERROR; false
        } else true
    }

    fun onDataSearch(tunesTitle: String): Boolean =
            if (list.contains(tunesTitle)) {
                true
            } else {
                onTunesInsert(tunesTitle)
                false
            }

    private fun getTunes() =
            Single.fromCallable {
                db.query(
                        DataBaseContract.TABLE_NAME,
                        arrayOf(DataBaseContract.MY_TUNES),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
            }

    fun readCursor()
            = getTunes()
            .map { cursor ->
                list.clear()
                cursor.moveToFirst()
                if (cursor.count > 0) (1..cursor.count).forEach {
                    list.add(cursor.getString(it))
                    this.list.clear()
                    this.list.addAll(list)
                }
                Log.d("list size", list.size.toString())
                cursor.close()
                list
            }
}