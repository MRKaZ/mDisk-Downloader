package com.mrkazofficial.mdiskdownloader.utils

import android.os.Environment
import com.mrkazofficial.mdiskdownloader.BuildConfig
import java.io.File

/**
 * @Project mDisk Downloader
 * @Class Constants
 * @Author MRKaZ
 * @Since 9:40 PM, 5/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

object Constants {

    const val BASE_URL = "https://diskuploader.entertainvideo.com/"

    @Suppress("DEPRECATION")
    val DOWNLOADS_FOLDER = File(Environment.getExternalStorageDirectory(), "Download")

    const val LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY =
        "${BuildConfig.APPLICATION_ID}.local.broadcast.download.list"
    const val LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY =
        "${BuildConfig.APPLICATION_ID}.local.broadcast.download.list.changed"


    const val LOCAL_BROADCAST_SERVICE_WORKER = "${BuildConfig.APPLICATION_ID}.service.worker"

    const val MESSENGER_TASK = "${BuildConfig.APPLICATION_ID}.messenger.task"
    const val ADD_DOWNLOAD_MESSENGER_TASK = "${BuildConfig.APPLICATION_ID}.messenger.add.download"
    const val SERVICE_ACTION_SHUTDOWN_ARG = "${BuildConfig.APPLICATION_ID}.service.shutdown.arg"

    const val DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG = "${BuildConfig.APPLICATION_ID}.notification.cancel.arg"
    const val DOWNLOAD_NOTIFICATION_TASK_ID_ARG = "${BuildConfig.APPLICATION_ID}.notification.task.id.arg"

    const val SAT_CONNECTION_ARG =
        "${BuildConfig.APPLICATION_ID}.service.activity.thorough.connection.arg"

    const val DOWNLOADER_URL_ARG = "${BuildConfig.APPLICATION_ID}.downloader.url.arg"
    const val DOWNLOADER_FILENAME_ARG = "${BuildConfig.APPLICATION_ID}.downloader.filename.arg"
    const val DOWNLOADER_PATH_ARG = "${BuildConfig.APPLICATION_ID}.downloader.path.arg"

    const val HTTP_ERROR_403 = "(caused by <HTTPError 403: 'Forbidden'>);"

}