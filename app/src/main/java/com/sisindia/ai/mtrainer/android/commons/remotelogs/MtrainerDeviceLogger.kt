package com.sisindia.ai.mtrainer.android.commons.remotelogs

import com.droidcommons.preference.Prefs
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

fun Throwable.getString() : String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    this.printStackTrace(pw)
    val sStackTrace: String = sw.toString()
    println(sStackTrace)
    return sStackTrace;

}

class Logger(val TAG: String) {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS a zzz", Locale.getDefault())
    private val date = dateFormat.format(Date(System.currentTimeMillis()))
    private val deviceDetails = DeviceDetails()

    private val deviceLogRef = Firebase.database.getReference("logs/$date/${deviceDetails.companyId}/${deviceDetails.trainerId}/deviceLogs")
    private val deviceDbRef = Firebase.database.getReference("logs/$date/${deviceDetails.companyId}/${deviceDetails.trainerId}/deviceDbLogs")

    @JvmOverloads fun log(logMsg: String, e: Throwable? = null){

        val timestamp = System.currentTimeMillis()
        val time = timeFormat.format(Date(timestamp))
        println("$time $TAG : ${logMsg}")
        println("Debuglogfirebase $time $TAG : ${logMsg} ${e?.getString()}")
        if(Prefs.getBoolean(PrefsConstants.IS_DEBUG, false)) {
            /*if(TAG.equals("ForcedSyncService")) {*/
                with(deviceLogRef) {
                    child(timestamp.toString()).setValue(
                        DeviceLog(
                            className = TAG,
                            msg = logMsg,
                            time = time,
                            exception = e?.getString() ?: "No Exception"
                        )
                    )
                }
            /*}*/
       }
    }
    @JvmOverloads fun logDb(data: Any?, dbTableName: String, e: Throwable? = null) {
        val timestamp = System.currentTimeMillis()
        val time = timeFormat.format(Date(timestamp))
        println(
            "$time $TAG : ${
            DeviceDbLog(
                className = TAG,
                time = time,
                dbData = data ?: "null",
                dbTableName = dbTableName,
                exception = e?.getString() ?: "No Exception"
            )
            }"
        )
        if(Prefs.getBoolean(PrefsConstants.IS_DEBUG, false)) {
            with(deviceDbRef) {
                child(timestamp.toString()).setValue(
                    DeviceDbLog(
                        className = TAG,
                        time = time,
                        dbData = data ?: "null",
                        dbTableName = dbTableName,
                        exception = e?.getString() ?: "No Exception"
                    )
                )
            }
        }
    }
}

data class DeviceLog(
    val className: String,
    val msg: String,
    val time: String,
    val exception: String = ""
)
data class DeviceDbLog(
    val dbData: Any,
    val time: String,
    val className: String,
    val dbTableName: String,
    val exception: String = ""
)