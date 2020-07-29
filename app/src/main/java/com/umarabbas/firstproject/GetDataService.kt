package com.umarabbas.firstproject

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GetDataService {

    @POST("store")
    fun sendData(@Body data:String) : Call<String>

    @Multipart
    @POST("screenshots/store")
    fun uploadScreenshots(@Part file : List<MultipartBody.Part>) : Call<String>
}