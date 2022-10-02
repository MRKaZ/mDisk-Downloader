package com.mrkazofficial.mdiskdownloader.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.ArrayMap
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mrkazofficial.mdiskdownloader.BuildConfig
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.activities.MainActivity
import com.mrkazofficial.mdiskdownloader.receivers.NotificationReceiver
import com.mrkazofficial.mdiskdownloader.tasks.Downloader
import com.mrkazofficial.mdiskdownloader.utils.Constants.ADD_DOWNLOAD_MESSENGER_TASK
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOADER_FILENAME_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOADER_PATH_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOADER_URL_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOAD_NOTIFICATION_TASK_ID_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.MESSENGER_TASK
import com.mrkazofficial.mdiskdownloader.utils.Constants.SAT_CONNECTION_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.SERVICE_ACTION_SHUTDOWN_ARG
import com.mrkazofficial.mdiskdownloader.utils.Utils.pendingIntentFlags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.lang3.RandomStringUtils

/**
 * @Project mDisk Downloader
 * @Class DownloaderService
 * @Author MRKaZ
 * @Since 2:38 AM, 9/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */
private const val TAG = "DownloaderService"

class DownloaderService : Service() {

    private var dServiceChannel = "$${BuildConfig.APPLICATION_ID}.downloader.service.channel"
    private var dServiceChannelNotificationId = 1337

    private var notificationChannel = "$${BuildConfig.APPLICATION_ID}.notification.channel"
    private var notificationId = 1997

    private lateinit var notificationManager: NotificationManager
    private var aMessenger: Messenger? = null
    private var sMessenger: Messenger = Messenger(ServiceMessageHandler(service = this))

    private var downloaderBuilderAMap: ArrayMap<String, Downloader> = ArrayMap()
    private var notificationBuilderMap: ArrayMap<String, ArrayMap<Int, NotificationCompat.Builder>> =
        ArrayMap()

    private lateinit var notificationManagerCompat: NotificationManagerCompat

    private var coroutineScopeBackground = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        notificationManagerCompat = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        startForeground(
            dServiceChannelNotificationId, createServiceNotification()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        aMessenger = intent?.getParcelableExtra(SAT_CONNECTION_ARG)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        aMessenger = intent?.getParcelableExtra(SAT_CONNECTION_ARG)
        return sMessenger.binder
    }

    override fun onRebind(intent: Intent?) {
        aMessenger = intent?.getParcelableExtra(SAT_CONNECTION_ARG)
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        aMessenger = null
        return if (super.onUnbind(intent))
            true
        else true
    }

    override fun onDestroy() {
        aMessenger = null
        super.onDestroy()
    }

    private class ServiceMessageHandler(
        private var service: DownloaderService
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.data[MESSENGER_TASK] as String) {
                ADD_DOWNLOAD_MESSENGER_TASK -> {
                    Log.d(TAG, "INCOMING MISSILE!!!")

                    val url = msg.data[DOWNLOADER_URL_ARG] as String?
                    val fileName = msg.data[DOWNLOADER_FILENAME_ARG] as String?
                    val downloadPath = msg.data[DOWNLOADER_PATH_ARG] as String?

                    if (url != null && fileName != null && downloadPath != null) {
                        service.makeDownloadRequest(
                            url = url,
                            fileName = fileName,
                            downloadPath = downloadPath
                        )
                    }
                }
                DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG -> {
                    (msg.data[DOWNLOAD_NOTIFICATION_TASK_ID_ARG] as String?).let {
                        if (it != null) {
                            service.cancelDownload(taskId = it)
                        }
                    }
                }
                SERVICE_ACTION_SHUTDOWN_ARG -> {
                    service.stopService()
                }
            }
        }
    }

    private fun makeDownloadRequest(url: String, fileName: String, downloadPath: String) {
        val taskId = "TASK_ID_${System.currentTimeMillis()}"
        Log.d(TAG, "REQUESTING DOWNLOAD [TASK_ID] -> $taskId")
        val downloader = Downloader(this).apply {
            dTaskId = taskId
            dUrl = url
            dFName = fileName
            dFPath = downloadPath
            downloaderService = this@DownloaderService
        }
        addTask(downloader = downloader)
        coroutineScopeBackground.launch {
            downloader.download()
        }
    }

    private fun cancelDownload(taskId: String) {
        if (taskId in downloaderBuilderAMap) {
            val downloader = downloaderBuilderAMap[taskId]
            downloader?.cancel()
            downloaderBuilderAMap.remove(taskId)
        }
    }

    private fun addTask(downloader: Downloader) {
        downloaderBuilderAMap[downloader.dTaskId] = downloader
    }

    private fun stopService() {
        Log.d(TAG, "STOPPING SERVICE!")
        stopForeground(true)
        stopSelf()
    }

    /** Notification area */

    /** Service notification */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createServiceNotificationChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Register the channel with the system
        notificationManager.createNotificationChannel(
            NotificationChannel(
                dServiceChannel,
                "${getString(R.string.app_name)} Downloader service",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(100)
            }
        )
    }

    private fun createServiceNotification(): Notification {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            createServiceNotificationChannel()
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            pendingIntentFlags()
        )

        val nBuilder = NotificationCompat.Builder(this, dServiceChannel).apply {
            setContentTitle(this@DownloaderService.getString(R.string.app_name))
            setSmallIcon(R.drawable.ic_logo_mdisk_transparent)
            setContentText("Service running")
            setDefaults(NotificationCompat.DEFAULT_ALL)
            // Set the intent that will fire when the user taps the notification
            setContentIntent(pendingIntent)
            setTicker(this@DownloaderService.getString(R.string.app_name))
            addAction(makeShutdownAction())
            setVibrate(longArrayOf(100))
        }

        return nBuilder.build()
    }

    private fun makeShutdownAction(): NotificationCompat.Action {
        val shutDownIntent = Intent(this, NotificationReceiver::class.java)
        shutDownIntent.action = SERVICE_ACTION_SHUTDOWN_ARG

        val shutDownPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            shutDownIntent,
            pendingIntentFlags()
        )

        return NotificationCompat.Action.Builder(
            0,
            "Shutdown",
            shutDownPendingIntent
        ).build()
    }

    /** Download notification */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Register the channel with the system
        notificationManager.createNotificationChannel(
            NotificationChannel(
                notificationChannel,
                "${getString(R.string.app_name)} Downloader notification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(100)
            }
        )
    }


    /** New notification for new download */
    fun newNotification(
        downloadTaskId: String,
        contentTitle: String,
        @ColorInt color: Int
    ) {
        val genNotificationId = notificationId++

        val cancelIntent = Intent(this, NotificationReceiver::class.java)
        cancelIntent.putExtra(DOWNLOAD_NOTIFICATION_TASK_ID_ARG, downloadTaskId)
        cancelIntent.action = DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG

        val pendingCancelIntent = PendingIntent.getBroadcast(
            this,
            genNotificationId,
            cancelIntent,
            pendingIntentFlags()
        )

        val nBuilder = NotificationCompat.Builder(this, notificationChannel).apply {
            setSmallIcon(android.R.drawable.stat_sys_download)
            setContentTitle(contentTitle)
            setColor(color)
            setProgress(0, 0, true)
            setOnlyAlertOnce(true)
            setTimeoutAfter(6000000)
            setOngoing(true)
            addAction(0, "Cancel", pendingCancelIntent)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setVibrate(longArrayOf(100))
        }

        val notificationBuilderAMap: ArrayMap<Int, NotificationCompat.Builder> = ArrayMap()
        notificationBuilderAMap[genNotificationId] = nBuilder

        notificationBuilderMap[downloadTaskId] = notificationBuilderAMap

        with(notificationManagerCompat) {
            // notificationId is a unique int for each notification that you must define
            notify(
                downloadTaskId,
                notificationBuilderMap.getValue(downloadTaskId).keyAt(0),
                nBuilder.build()
            )
        }
    }

    /** Updating downloading progress*/
    fun updateDownloadProgress(
        downloadTaskId: String,
        progress: Int,
        progressText: String,
        bigText: String
    ) {
        val getNBuilder = notificationBuilderMap.getValue(downloadTaskId).getValue(
            notificationBuilderMap.getValue(downloadTaskId).keyAt(
                0
            )
        )

        getNBuilder.apply {
            setSubText(progressText)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        }

        if (progress == -1 || progress < 0) {
            getNBuilder.apply {
                setProgress(
                    100,
                    0,
                    true
                )
            }
        } else {
            getNBuilder.apply {
                setProgress(
                    100,
                    progress,
                    false
                )
                setContentText("$progress%")
            }
        }

        with(notificationManagerCompat) {
            // notificationId is a unique int for each notification that you must define
            notify(
                downloadTaskId,
                notificationBuilderMap.getValue(downloadTaskId).keyAt(0),
                getNBuilder.build()
            )
        }
    }

    /** Ending notification with a reason */
    fun downloadEndNotification(
        downloadTaskId: String,
        fileName: String,
        status: String,
        reason: String?,
        @ColorInt
        color: Int,
        @DrawableRes
        icon: Int
    ) {
        if (downloadTaskId in notificationBuilderMap) {
            with(notificationManagerCompat) {
                cancel(
                    downloadTaskId,
                    notificationBuilderMap.getValue(downloadTaskId).keyAt(0)
                )
            }
        }


        val nBuilder = NotificationCompat.Builder(this, notificationChannel).apply {
            setContentTitle(fileName)
            setColor(color)
            setSmallIcon(icon)
            setContentText(status)
            if (reason != null) {
                setStyle(NotificationCompat.BigTextStyle().bigText(reason))
            }
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setSound(null)
            setTicker(fileName)
            setVibrate(longArrayOf(100))
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        with(notificationManagerCompat) {
            // notificationId is a unique int for each notification that you must define
            notify(
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphanumeric(8).hashCode(),
                nBuilder.build()
            )
        }

        if (downloadTaskId in notificationBuilderMap)
            notificationBuilderMap.remove(downloadTaskId)
    }
}
