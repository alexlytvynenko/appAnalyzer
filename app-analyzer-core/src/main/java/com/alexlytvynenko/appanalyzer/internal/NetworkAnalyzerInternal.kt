package com.alexlytvynenko.appanalyzer.internal

import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.*
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import com.alexlytvynenko.appanalyzer.internal.entity.LogEntity
import com.alexlytvynenko.appanalyzer.internal.entity.LogEntityParser
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntityParser
import org.jetbrains.anko.db.*
import java.lang.ref.WeakReference
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alex_litvinenko on 09.10.17.
 */
internal object NetworkAnalyzerInternal {

    private val fileIoExecutor = newSingleThreadExecutor("File-IO")
    private var appContext: WeakReference<Context>? = null

    internal var disabledLogs = false
    internal var disabledExceptions = false
    internal var disabledRequests = false
    internal var isNetworkInterceptorInited = false

    fun setEnabled(context: Context, componentClass: Class<*>, enabled: Boolean) {
        val appContext = context.applicationContext
        this.appContext = WeakReference(appContext)
        executeOnFileIoThread(Runnable { setEnabledBlocking(appContext, componentClass, enabled) })
    }

    internal fun runLogObserver() {

        val processId = Integer.toString(android.os.Process.myPid())

        try {
            Runtime.getRuntime().exec("logcat -c")

            val command = "logcat -v threadtime *:V"

            val process = Runtime.getRuntime().exec(command)

            val bufferedReader = BufferedReader(
                    InputStreamReader(process.inputStream))

            var line = bufferedReader.readLine()
            while (line != null) {
                if (line.contains(processId)) {
                    val splitLine = line.split(" ").filter { !TextUtils.isEmpty(it) }
                    val dateString = splitLine[0]
                    val timeString = splitLine[1]
                    val date = dateFromString("$dateString $timeString")
                    val processString = splitLine[2]
                    val threadString = splitLine[3]
                    val processThread = "$processString-$threadString"
                    val priority = splitLine[4]
                    var tag = splitLine[5]
                    var text = splitLine.subList(6, splitLine.size).joinToString(" ")
                    if (tag.endsWith(":")) {
                        tag = tag.substring(0, tag.length - 1).trim()
                    } else if (text.startsWith(":")) {
                        text = text.substring(1, text.length).trim()
                    }
                    if (text.isEmpty()) {
                        line = bufferedReader.readLine()
                        continue
                    }
                    val logEntity = LogEntity(System.nanoTime(), date.time, processThread, priority,
                            tag, text)
                    saveLogToDatabase(logEntity)
                    if (priority == "E") saveLogToDatabase(logEntity, isException = true)
                }
                line = bufferedReader.readLine()
            }
        } catch (ex: IOException) {
            Log.e("NetworkAnalyzerInternal", "Listening logs is failed", ex)
        }
    }

    internal fun executeOnFileIoThread(runnable: Runnable) {
        fileIoExecutor.execute(runnable)
    }

    private fun setEnabledBlocking(appContext: Context, componentClass: Class<*>, enabled: Boolean) {
        val component = ComponentName(appContext, componentClass)
        val newState = if (enabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED
        // Blocks on IPC.
        appContext.packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP)
    }

    private fun newSingleThreadExecutor(threadName: String): Executor {
        return Executors.newSingleThreadExecutor(NetworkAnalyzerSingleThreadFactory(threadName))
    }

    internal fun saveRequestToDatabase(requestEntity: RequestEntity, replace: Boolean = false) {
        with(requestEntity) {
            val values = ContentValues()
            values.put(RequestEntity.COLUMN_METHOD, method)
            values.put(RequestEntity.COLUMN_ID, id)
            values.put(RequestEntity.COLUMN_START_DATE, startDateInMS)
            values.put(RequestEntity.COLUMN_URL, url)
            values.put(RequestEntity.COLUMN_REQUEST_HEADERS, convertToBytes(requestHeaders))
            values.put(RequestEntity.COLUMN_REQUEST_BODY, requestBody)
            values.put(RequestEntity.COLUMN_STATUS, status)
            values.put(RequestEntity.COLUMN_DURATION, duration)
            values.put(RequestEntity.COLUMN_RESPONSE_HEADERS, convertToBytes(responseHeaders))
            values.put(RequestEntity.COLUMN_RESPONSE_BODY, responseBody)
            appContext?.get()?.database?.use {
                if (replace) replace(RequestEntity.TABLE_NAME, null, values)
                else insert(RequestEntity.TABLE_NAME, null, values)
            }
        }
    }

    internal fun loadRequestsFromDatabase() = appContext?.get()?.database?.use {
        select(RequestEntity.TABLE_NAME).exec { parseList(RequestEntityParser()) }
    }

    internal fun deleteRequestsFromDatabase() = appContext?.get()?.database?.use {
        delete(RequestEntity.TABLE_NAME)
    }

    internal fun deleteRequestFromDatabase(request: RequestEntity) = appContext?.get()?.database?.use {
        delete(RequestEntity.TABLE_NAME, "${RequestEntity.COLUMN_ID} = {id}", "id" to request.id)
    }

    internal fun saveLogToDatabase(logEntity: LogEntity, isException: Boolean = false) {
        with(logEntity) {
            val values = ContentValues()
            values.put(LogEntity.COLUMN_ID, id)
            values.put(LogEntity.COLUMN_START_DATE, startDateInMS)
            values.put(LogEntity.COLUMN_PROCESS_THREAD, processThread)
            values.put(LogEntity.COLUMN_PRIORITY, priority)
            values.put(LogEntity.COLUMN_TAG, tag)
            values.put(LogEntity.COLUMN_TEXT, text)
            appContext?.get()?.database?.use {
                val tableName =
                        if (isException) LogEntity.EXCEPTION_TABLE_NAME
                        else LogEntity.LOG_TABLE_NAME
                insert(tableName, null, values)
            }
        }
    }

    internal fun loadLogsFromDatabase(isException: Boolean = false) = appContext?.get()?.database?.use {
        select(if (isException) LogEntity.EXCEPTION_TABLE_NAME
        else LogEntity.LOG_TABLE_NAME)
                .exec { parseList(LogEntityParser()) }
    }

    internal fun deleteLogsFromDatabase(isException: Boolean = false) = appContext?.get()?.database?.use {
        delete(if (isException) LogEntity.EXCEPTION_TABLE_NAME
        else LogEntity.LOG_TABLE_NAME)
    }

    internal fun deleteLogFromDatabase(log: LogEntity, isException: Boolean = false) = appContext?.get()?.database?.use {
        delete(if (isException) LogEntity.EXCEPTION_TABLE_NAME
        else LogEntity.LOG_TABLE_NAME, "${LogEntity.COLUMN_ID} = {id}", "id" to log.id)
    }

    @Throws(IOException::class)
    internal fun convertToBytes(obj: Any): ByteArray {
        ByteArrayOutputStream().use({ bos ->
            ObjectOutputStream(bos).use({ out ->
                out.writeObject(obj)
                return bos.toByteArray()
            })
        })
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    internal fun convertFromBytes(bytes: ByteArray): Any {
        ByteArrayInputStream(bytes).use({ bis -> ObjectInputStream(bis).use({ input -> return input.readObject() }) })
    }

    internal fun saveToFile(context: Context, text: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val logFileName = "file_" + timeStamp
        val logFile = File.createTempFile(
                logFileName,           /* prefix */
                ".rtf",         /* suffix */
                context.cacheDir      /* directory */
        )
        try {
            val fileOutputStream = FileOutputStream(logFile, true)
            fileOutputStream.write(text.toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return logFile
    }

    internal fun shareFile(activity: Activity, file: File, titleKey: String) {
        if (file.exists()) {
            val intentShareFile = Intent(Intent.ACTION_SEND)
            intentShareFile.type = "application/rtf"
            val fileUri = if (Build.VERSION.SDK_INT > 21) {
                FileProvider.getUriForFile(activity, activity.packageName + ".fileprovider", file)
            } else {
                Uri.fromFile(file)
            }

            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing $titleKey of ${activity.packageName}")
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing $titleKey...")
            activity.startActivity(Intent.createChooser(intentShareFile, "Share $titleKey"))
        }
    }
}