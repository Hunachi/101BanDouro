package com.example.hanah.dao

import android.os.Parcel
import android.os.Parcelable
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table

@Table
class Tunes{

    @PrimaryKey(autoincrement = true)
    var id = 0

    @Column(indexed = true)
    var tunes = ""

    @Column(indexed = true)
    var explanation = ""

}