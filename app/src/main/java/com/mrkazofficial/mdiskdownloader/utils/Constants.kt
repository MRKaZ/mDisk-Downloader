package com.mrkazofficial.mdiskdownloader.utils

import android.os.Environment
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
    const val INFO_SESSION_MAIN_KEY_SHARED_PREF = "com.mrkazofficial.mdiskdownloader"
    const val INFO_SESSION_HIDE_KEY_SHARED_PREF =
        "com.mrkazofficial.mdiskdownloader.hide.note.shared.pref"
    val DOWNLOADS_FOLDER = File(Environment.getExternalStorageDirectory(), "Download")

    const val LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY =
        "com.mrkazofficial.mdiskdownloader.local.broadcast.download.list"
    const val LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY =
        "com.mrkazofficial.mdiskdownloader.local.broadcast.download.list.changed"

}