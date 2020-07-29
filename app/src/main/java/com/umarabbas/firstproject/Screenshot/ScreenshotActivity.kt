package com.umarabbas.firstproject.Screenshot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.umarabbas.firstproject.R
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log

class ScreenshotActivity : AppCompatActivity() {

    private var mediaProjectionManager: MediaProjectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SSTheme)
//        setContentView(R.layout.activity_screenshot)
//        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, SSService::class.java))
                mediaProjectionManager =
                        (getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager)
                startActivityForResult(
                        mediaProjectionManager?.createScreenCaptureIntent(),
                        1
                )
            } else {
                val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("shani-image","on result the images are been taken")
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                SSService.mScreenDensity = metrics.densityDpi
                SSService.setProjectionHandler(mediaProjectionManager?.getMediaProjection(resultCode, data!!))
                this.finish()
            }
        }
    }
}
