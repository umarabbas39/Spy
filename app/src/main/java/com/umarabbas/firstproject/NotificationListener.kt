package com.umarabbas.firstproject

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.io.File
import java.text.DateFormat
import java.util.*

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        when(sbn?.packageName){
            "com.whatsapp" -> {
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.getString("android.title")
                val msg = sbn.notification.extras.getString("android.text")
                val msgLog = File(applicationContext.getExternalFilesDir(null),
                        "whatsapp.txt")
                if(!msgLog.exists()){
                    msgLog.createNewFile()
                }
                msgLog.appendText("$sender¬$msg¬$date\n")
            }
            "com.instagram.android" -> {
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.getString("android.title")
                val msg = sbn.notification.extras.getString("android.text")
                val file = File(applicationContext.getExternalFilesDir(null),"instagram.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$date\n")
            }
            "com.google.android.gm" -> {
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.getString("android.title")
                val msg = sbn.notification.extras.get("android.text")
                val bigText = sbn.notification.extras.get("android.bigText")
                val file = File(applicationContext.getExternalFilesDir(null),"gmail.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$bigText¬$date\n\n")
//                val bundle: Bundle = sbn.notification.extras ?: return
//
//                val keys = bundle.keySet()
//                val it = keys.iterator()
//                val file = File(applicationContext.getExternalFilesDir(null),"gmail.txt")
//                file.createNewFile()
//
//                while (it.hasNext()) {
//                    val key = it.next()
//                    file.appendText("[" + key + "=" + bundle.get(key)+"]")
//                }
            }
            "com.twitter.android" ->{
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.get("android.title")
                val msg = sbn.notification.extras.get("android.text")
                val file = File(applicationContext.getExternalFilesDir(null),"twitter.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$date\n")
            }
            "org.telegram.messenger" ->{
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.get("android.title")
                val msg = sbn.notification.extras.get("android.text")
                val file = File(applicationContext.getExternalFilesDir(null),"telegram.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$date\n")
            }
            "com.tinder" -> {
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.get("android.title")
                val msg = sbn.notification.extras.get("android.text")
                val file = File(applicationContext.getExternalFilesDir(null),"tinder.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$date\n")
            }
            "com.paypal.android.p2pmobile" -> {
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.get("android.title")
                val msg = sbn.notification.extras.get("android.text")
                val file = File(applicationContext.getExternalFilesDir(null),"paypal.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$date\n")
            }
            "com.xbetone.proversion" -> {
                val date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date())
                val sender = sbn.notification.extras.get("android.title")
                val msg = sbn.notification.extras.get("android.text")
                val file = File(applicationContext.getExternalFilesDir(null),"1xbet.txt")
                if(!file.exists()){
                    file.createNewFile()
                }
                file.appendText("$sender¬$msg¬$date\n")
            }
        }
    }
}