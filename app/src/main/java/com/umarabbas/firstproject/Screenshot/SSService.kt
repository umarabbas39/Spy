package com.umarabbas.firstproject.Screenshot

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.drawable.Icon
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.umarabbas.firstproject.R
import java.io.File
import java.io.FileOutputStream

class SSService : Service() {

    private val ONGOING_NOTIFICATION_ID = 667
    private val CHANNEL_ID = "212_Wal"
    private val TAG = "MyService"

    companion object {
        var projectionM: MediaProjection? = null
        fun setProjectionHandler(projection: MediaProjection?) {
            projectionM = projection
            handler?.invoke()
        }
        var handler:(()->Unit)? = null
        var mScreenDensity = 0
        var mDisplayWidth = 1200
        var mDisplayHeight = 1600
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    "TestingSS",
                    importance
            )
            channel.description = "This is a WalRex Notification channel"
            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
            val pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, 0
            )
            val notification: Notification = Notification.Builder(
                    this,
                    CHANNEL_ID
            )
                    .setContentTitle("testing screen shot")
                    .setContentText("R.string.notification_message")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(
                            Icon.createWithResource(
                                    this,
                                    R.drawable.ic_launcher_background
                            )
                    )
                    .setContentIntent(pendingIntent)
                    .setTicker("getText(R.string.ticker_text)")
                    .build()
            if (intent!!.getIntExtra("stop", 5) == 6) {
                Log.d(TAG, "onStartCommand: second if is executed")
                startForeground(ONGOING_NOTIFICATION_ID, notification)
                stopForeground(true)
                stopSelf()
            } else {
                startForeground(ONGOING_NOTIFICATION_ID, notification)
            }
        }
        handler = { saveShowS() }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        projectionM?.stop()
    }

//    override fun onCreate() {
//        super.onCreate()
//        Handler().postDelayed({
//            Log.d("shani-image", "the images are been taken")
//            saveShowS()
//        }, 2000)
//    }

    fun saveShowS() {
        val reader = ImageReader.newInstance(mDisplayWidth, mDisplayHeight, PixelFormat.RGBA_8888, 2)
        reader.setOnImageAvailableListener(
                { readerM ->
                    val image = readerM.acquireLatestImage()
                    val planes = image?.let{image.getPlanes()}
                    val buffer = planes!![0].getBuffer()
                    val pixelStride: Int = planes[0].getPixelStride()
                    val rowStride: Int = planes[0].getRowStride()
                    val rowPadding: Int = rowStride - pixelStride * mDisplayWidth
                    val bitmap = Bitmap.createBitmap(
                            mDisplayWidth + rowPadding / pixelStride,
                            mDisplayHeight,
                            Bitmap.Config.ARGB_8888
                    )
                    Log.d("shani-image", "the images are been taken")
                    bitmap.copyPixelsFromBuffer(buffer)
                    val file = File(applicationContext.getExternalFilesDir(null),"/ScreenShot/")
                    if (!file.exists()) {
                        file.mkdirs()
                        file.mkdir()
                    }
                    val img = File(file, "${System.nanoTime()}.jpg")
                    val fos = FileOutputStream(img)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()
                    image.close()
                    stopSelf()
                    stopForeground(true)
                }, null
        )
        projectionM?.createVirtualDisplay(
                "ScreenShot",
                mDisplayWidth,
                mDisplayHeight,
                mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                reader.surface, null, null
        )
    }
}
