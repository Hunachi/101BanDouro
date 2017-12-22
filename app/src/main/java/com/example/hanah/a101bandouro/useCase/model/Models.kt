package com.example.hanah.a101bandouro.useCase.model

/**
 * Created by hanah on 2017/11/11.
 */

data class Result(
        val ResultSet: ResultSet
)

data class ResultSet(
        val apiVersion: String,
        val engineVersion: String,
        val Point: com.example.hanah.a101bandouro.useCase.model.Point
)

data class Point(
        val Prefecture: Prefecture,
        val GeoPoint: GeoPoint,
        val Distance: Int,
        val Station: Station
)

data class Prefecture(val code: Int, val Name: String)

data class GeoPoint(
        val gcs: String,
        val lati_d: Double,
        val longi_d: Double,
        val lati: String,
        val longi: String
)

data class Station(
        val code: Long,
        val Name: String,
        val Yomi: String/*,
        val Type: List<String> = listOf()*/
)

data class MemoryItem(
        var tuneName: String = "hoge"
)