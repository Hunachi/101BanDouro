package com.example.hanah.a101bandouro.tool

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

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
        val DATABASE_VERSION = 0
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

    fun onTunesInsert(tunesTitle: String): Boolean {
        val value = ContentValues()
        value.put(DataBaseContract.MY_TUNES, tunesTitle)
        val id = db.insert(DataBaseContract.TABLE_NAME, null, value)
        return if (id < 0) {
            if(db.isOpen)db.close()
            Log.ERROR; false
        } else {
            if(db.isOpen)db.close()
            true
        }
    }

    fun getTunes(): MutableList<String> {
        return readCursor(
                db.query(
                        DataBaseContract.TABLE_NAME,
                        arrayOf("_id", DataBaseContract.MY_TUNES),
                        null,
                        null,
                        null,
                        null,
                        "_id desc",
                        null
                ))
    }

    private fun readCursor(cursor: Cursor): MutableList<String> {
        val list = mutableListOf<String>()
        cursor.moveToFirst()
        (1..cursor.count).forEach {
            list.add(cursor.getString(it))
        }
        Log.d("list size", list.size.toString())
        return list
    }

}