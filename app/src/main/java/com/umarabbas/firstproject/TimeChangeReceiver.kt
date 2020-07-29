package com.umarabbas.firstproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.umarabbas.firstproject.Screenshot.SSReceiver

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.TIME_SET"){
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
        }
    }
}
