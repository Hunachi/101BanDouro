package com.example.hanah.a101bandouro.client

/**
 * Created by hanah on 2017/11/11.
 */
import com.example.hanah.a101bandouro.model.ResultSet
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by hanah on 2017/11/11.
 */
interface Server{

    @GET("/v1/json/geo/station")
    fun findStations(
            @Query("key")key: String = "",
            @Query("geoPoint")geoPoint: String = ""
    ): Observable<ResultSet>

}