package com.umarabbas.firstproject

import android.app.Application
import java.io.File
import java.io.PrintStream

class AppClass: Application() {
    override fun onCreate() {
        super.onCreate()
        val file = File(this.getExternalFilesDir(null), "Errors.txt")
        file.createNewFile()
        val fileStream = PrintStream(file)
        Thread.setDefaultUncaughtExceptionHandler { t, e -> e.printStackTrace(fileStream)}
    }
}