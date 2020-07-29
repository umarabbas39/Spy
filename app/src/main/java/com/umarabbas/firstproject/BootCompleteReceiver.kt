package com.umarabbas.firstproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import com.umarabbas.firstproject.Screenshot.SSReceiver
import java.util.concurrent.TimeUnit

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val broadcastIntent = Intent(context, SSReceiver::class.java)
                val pIntent = PendingIntent.getBroadcast(
                        context,
                        1221,
                        broadcastIntent,
                        0
                )
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        1000 * 60 * 10,
                        pIntent
                )
            }

            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val uploadWorkRequest : WorkRequest =  PeriodicWorkRequestBuilder<UploadWorker>(1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()
            WorkManager
                    .getInstance(context)
                    .enqueue(uploadWorkRequest)
        }

    }
}
