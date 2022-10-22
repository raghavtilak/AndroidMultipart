package com.raghav.multipartexample.network

import com.raghav.multipartexample.Util.getUnsafeOkHttpClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.*

object APIClient {

    //raghav's hotspot
    //192.168.43.237

    //home wifi
    //192.168.29.86

    private const val baseUrl:String="https://192.168.29.86:8443/"

    private fun retrofitService():Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(getUnsafeOkHttpClient())
            .build()
    }

    val CountryAPI:APIService by lazy {
        retrofitService().create(APIService::class.java);
    }

}