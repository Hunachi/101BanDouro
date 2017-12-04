package com.example.hanah.a101bandouro.tool

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * 🍣 Created by hanah on 2017/12/04.
 */
class FeedReaderContract{

    companion object {
        val TABLE_NAME = "101BanDouro"
        val MY_TUNES = "tunes"
        val TUNES_DETAIL = "detail"
        val SQL_CREATE_ENTRIES = "CREATE LIST OF $TABLE_NAME ( $MY_TUNES , $TUNES_DETAIL );"
        val SQL_DELETE_ENTRIES = "DELETE LIST OF $TABLE_NAME ( $MY_TUNES , $TUNES_DETAIL );"
    }

}

class FeedReaderDbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        val DATABASE_NAME = "101BanDouro.db"
        val DATABASE_VERSION = 0
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(FeedReaderContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(FeedReaderContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        onUpgrade(db, newVersion, oldVersion)
    }

}