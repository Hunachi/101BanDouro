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
        val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ( $MY_TUNES text )"
        val SQL_DELETE_ENTRIES = "hoge"
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
    db.query(
    DataBaseContract.TABLE_NAME,            /*テーブル名*/
    arrayOf(DataBaseContract.MY_TUNES),     /*返してほしい列*/
    DataBaseContract.MY_TUNES + " = ?",     /*制約の列名*/
    arrayOf("東京"),                         /*制約の当てはまる条件*/
    null,                                   /*行のグループ*/
    null,                                   /*行のグループでのフィルター*/
    DataBaseContract.MY_TUNES + " DESC"     /*ソートのパラメータ*/
    )
}

class DatabaseModel(context: Context) {

    private val helper: DataBaseHelper = DataBaseHelper(context)
    val db: SQLiteDatabase = helper.writableDatabase

    fun onTunesInsert(tunesTitle: String): Boolean {

        val value = ContentValues()

        value.put(DataBaseContract.MY_TUNES, tunesTitle)

        val id = db.insert(DataBaseContract.TABLE_NAME, null, value)


        /*fun onDataSearch(tunesTitle: String): Boolean
                if (list.contains(tunesTitle)) {
                    true
                } else {
                    onTunesInsert(tunesTitle)
                    false
                }*/
        return true
    }

    fun getTunes() =

    /*db.query(
                        DataBaseContract.TABLE_NAME,            /*テーブル名*/
                        arrayOf(DataBaseContract.MY_TUNES),     /*返してほしい列*/
                        DataBaseContract.MY_TUNES + " = ?",     /*制約の列名*/
                        arrayOf("東京"),                         /*制約の当てはまる条件*/
                        null,                                   /*行のグループ*/
                        null,                                   /*行のグループでのフィルター*/
                        DataBaseContract.MY_TUNES + " DESC"     /*ソートのパラメータ*/
                )*/

    fun readCursor()
            = getTunes()
    /*.map { cursor ->
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
    }*/
}