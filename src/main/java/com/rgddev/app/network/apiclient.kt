package com.rgddev.app.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.GsonBuilder
import com.rgddev.app.utils.AppConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {

    private var retrofit: Retrofit? = null
    private var retrofit22: Retrofit? = null
    private var INSTANCE: Retrofit? = null
    private val okHttpClient1 = OkHttpClient().newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        //            .addNetworkInterceptor(new StethoInterceptor())
        .build()

    fun getInstance(): Retrofit? {


        if (INSTANCE == null) {
            synchronized(Retrofit::class.java) {

                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(logging)
                httpClient.readTimeout(60, TimeUnit.SECONDS)
                httpClient.connectTimeout(60, TimeUnit.SECONDS)
                httpClient.retryOnConnectionFailure(true)

                val gsonBuilder = GsonBuilder()
                gsonBuilder.serializeNulls()
                gsonBuilder.setPrettyPrinting()
                val gson = gsonBuilder.create()

                INSTANCE = Retrofit.Builder()
                    .baseUrl(AppConfig.URL.BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }

        }

        return INSTANCE
    }

    val client: Retrofit?
        get() {
//            val gson = GsonBuilder()
//                .setLenient()
//                .create()
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()


            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .client(okHttpClient1)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(AppConfig.URL.BASE_URL)
                    .build()

            }
            return retrofit
        }



}
