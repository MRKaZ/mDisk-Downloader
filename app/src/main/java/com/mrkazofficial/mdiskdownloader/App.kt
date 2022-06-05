package com.mrkazofficial.mdiskdownloader

import android.app.Application
import com.mrkazofficial.mdiskdownloader.tasks.DownloadManager
import com.mrkazofficial.mdiskdownloader.utils.SharedPreferenceUtils

/**
 * @Project mDisk Downloader
 * @Class App
 * @Author MRKaZ
 * @Since 4:00 PM, 5/29/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        /** Register on download complete receiver */
        DownloadManager.registerReceiver(context = this)
        /** Initiate shared preference */
        SharedPreferenceUtils.initiate(context = this)
    }
}