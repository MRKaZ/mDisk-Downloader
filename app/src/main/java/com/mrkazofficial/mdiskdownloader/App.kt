package com.mrkazofficial.mdiskdownloader

import android.app.Application
import android.util.Log
import com.mrkazofficial.mdiskdownloader.tasks.DownloadManager
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

/**
 * @Project mDisk Downloader
 * @Class App
 * @Author MRKaZ
 * @Since 4:00 PM, 5/29/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

private const val TAG = "App"

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        /** Register on download complete receiver */
        DownloadManager.registerReceiver(context = this)
        try {
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
            Aria2c.getInstance().init(this)
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }
    }
}