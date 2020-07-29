package com.umarabbas.firstproject.Screenshot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val activity = Intent(context, ScreenshotActivity::class.java)
        activity.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        activity.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        context.startActivity(activity)
        Log.d("SSReceiver","SSReceiver run")
    }
}
