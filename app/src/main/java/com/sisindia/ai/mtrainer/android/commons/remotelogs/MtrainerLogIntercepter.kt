package com.sisindia.ai.mtrainer.android.commons.remotelogs

import android.util.Log
import com.droidcommons.preference.Prefs
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ------------------------------------------------------
// InputStream -> String (unchanged)
// ------------------------------------------------------
fun InputStream.getString(): String {
    val bufferSize = 1024
    val buffer = CharArray(bufferSize)
    val out = StringBuilder()
    val `in`: Reader = InputStreamReader(this, StandardCharsets.UTF_8)
    var charsRead: Int
    while (true) {
        try {
            if (`in`.read(buffer, 0, buffer.size).also { charsRead = it } <= 0) break
            out.append(buffer, 0, charsRead)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return out.toString()
}

// ------------------------------------------------------
// SAFE: Request.getStringBody()
// No crash on GET or null body
// ------------------------------------------------------
fun Request.getStringBody(): String {
    return try {
        val copy = this.newBuilder().build()
        val body = copy.body ?: return "NO_REQUEST_BODY"
        val buffer = Buffer()
        body.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: Exception) {
        "Unable to convert Request Body"
    }
}

// ------------------------------------------------------
// SAFE: bodyToString() for UploadFile logs
// ------------------------------------------------------
private fun bodyToString(request: Request): String {
    return try {
        val copy = request.newBuilder().build()
        val body = copy.body ?: return "NO_REQUEST_BODY"
        val buffer = Buffer()
        body.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: Exception) {
        "did not work"
    }
}

class MtrainerLogIntercepter @Inject constructor() : Interceptor {

    private val TAG = "MtrainerLogIntercepter"
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS a zzz", Locale.getDefault())
    private val date = dateFormat.format(Date(System.currentTimeMillis()))
    private val deviceDetails = DeviceDetails()
    private val logRef = Firebase.database.getReference(
        "logs/$date/${deviceDetails.companyId}/${deviceDetails.trainerId}"
    )

    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request()
        val response = chain.proceed(request)

        var contentType: MediaType? = null
        var bodyString: String? = null

        // ------------------------------------------------------
        // SAFE: Read response body without crash
        // ------------------------------------------------------
        val responseBody = response.body
        if (responseBody != null) {
            contentType = responseBody.contentType()
            bodyString = try {
                responseBody.string()
            } catch (e: Exception) {
                "ERROR_READING_RESPONSE"
            }
        } else {
            bodyString = "NO_RESPONSE_BODY"
        }

        // ------------------------------------------------------
        // Prepare Log Object
        // ------------------------------------------------------
        val timestamp = System.currentTimeMillis()
        val time = timeFormat.format(Date(timestamp))

        val logData = try {
            LogResponse(
                requestBody =
                    if (response.request.url.toString().contains("UploadFile"))
                        "BINARY_FILE " + bodyToString(request)
                    else request.getStringBody(),

                responseBody = bodyString,
                url = response.request.url.toString(),
                statusCode = response.code,
                statusMsg = response.message,
                time = time
            )
        } catch (e: Exception) {
            LogResponse(
                requestBody =
                    if (response.request.url.toString().contains("UploadFile"))
                        "BINARY_FILE"
                    else request.getStringBody(),

                responseBody = bodyString,
                url = response.request.url.toString(),
                statusCode = response.code,
                statusMsg = response.message,
                time = time
            )
        }

        // ------------------------------------------------------
        // Firebase Upload
        // ------------------------------------------------------
        if (Prefs.getBoolean(PrefsConstants.IS_DEBUG, false)) {
            Log.e(TAG, "Firebase Log Object -> $logData")
            with(logRef) {
                updateChildren(mapOf(Pair("-DeviceDetails", deviceDetails)))
                child(timestamp.toString()).setValue(logData)
            }
        }

        // ------------------------------------------------------
        // Rebuild consumed response body
        // ------------------------------------------------------
        return if (contentType != null) {
            val rebuiltBody = ResponseBody.create(contentType, bodyString ?: "")
            response.newBuilder().body(rebuiltBody).build()
        } else {
            response
        }
    }
}

// ------------------------------------------------------
// Response Log Model
// ------------------------------------------------------
data class LogResponse(
    val requestBody: String?,
    val url: String,
    val statusCode: Int?,
    val statusMsg: String?,
    val time: String?,
    val responseBody: String?
)
