package com.example.hanah.a101bandouro.useCase.client

/**
 * Created by hanah on 2017/11/11.
 */
import com.example.hanah.a101bandouro.useCase.model.Result
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

/**
 * Created by hanah on 2017/11/11.
 */
class ServerClient(key: String = "") {

    var key = key
        private set

    private val server: Server

    init {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpCliet = OkHttpClient.Builder()
        httpCliet.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.ekispert.jp")
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        server = retrofit.create(Server::class.java)
    }

    fun findStation(key: String, geoPoint: String): Observable<Result> = server.findStations(key, geoPoint)

    fun findStation(x: Double, y: Double)
            = findStation(key, x.toString() + "," + y.toString() )

    private class StringConverterFactory : Converter.Factory() {

        override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
            if (String::class.java == type) {
                return Converter<ResponseBody, String> { value -> value.string() }
            }
            return null
        }

        override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
            if (String::class.java == type) {
                return Converter<String, RequestBody> { value -> RequestBody.create(MEDIA_TYPE, value) }
            }

            return null
        }

        companion object {
            private val MEDIA_TYPE = MediaType.parse("text/plain")

            fun create(): StringConverterFactory {
                return StringConverterFactory()
            }
        }

    }
}