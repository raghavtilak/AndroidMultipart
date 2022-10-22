package com.raghav.multipartexample.network

import android.media.Image
import com.raghav.multipartexample.Country
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.lang.reflect.Array

interface APIService {

    @GET("/getCountries")
    fun getCountries():Call<List<Country>>

    /*
    * was getting this error on the server side,
    * com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 2 path $
java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 2 path $
    * This is becoz i was sending JSON, but retrofit was wrapping it into string, so
    * instead of having my first letter of JSON as '{' or '[', the server was receinvg
    * the JSON with first letter as '"'. so on the server side the gson was not able to
    * deserialize it.
    *
    * we either remove the '"' at the beginning or at end, or we can either send from here
    * only a valid JSON not wrapped in as a String object
    *
    * we are doing 1st option
    * */
    @Multipart
    @POST("/addCountry")
    fun addCountry(@Part images: List<MultipartBody.Part>,@Part("country") country:RequestBody):Call<String>

    @Multipart
    @POST("/addImage")
    fun addImage(@Part images: MultipartBody.Part,@Part("name") name:String):Call<String>

    @Multipart
    @POST("/addImages")
    fun addImages(@Part images: List<MultipartBody.Part>):Call<String>


//    @GET("/download/{image}")
//    fun getImage(@Query("image")):Call<Multipart>
}