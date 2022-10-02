package com.mrkazofficial.mdiskdownloader.tasks

import android.content.Context
import android.util.Log
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.services.DownloaderService
import com.mrkazofficial.mdiskdownloader.utils.Constants.HTTP_ERROR_403
import com.mrkazofficial.mdiskdownloader.utils.Utils.toColor
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File

/**
 * @Project mDisk Downloader
 * @Class Downloader
 * @Author MRKaZ
 * @Since 4:21 PM, 9/23/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */
private const val TAG = "Downloader"
private const val BUFFER_SIZE = 1024

class Downloader constructor(
    private val context: Context
) {

    private var isCancelled = false
    lateinit var dTaskId: String
    lateinit var dUrl: String
    lateinit var dFName: String
    lateinit var dFPath: String
    lateinit var downloaderService: DownloaderService

    fun download() {
        downloaderService.newNotification(
            downloadTaskId = dTaskId,
            contentTitle = dFName,
            color = context.toColor(color = R.color.colorSecondaryDark)
        )

        val tmpFile = File.createTempFile(
            context.getString(R.string.app_name),
            null,
            context.externalCacheDir
        )

        /** Library Credits
         * @Library youtubedl-android
         * @Author yausername
         * @Contributors xibr, JunkFood02, p3g4asus, tinybug
         * @Link https://github.com/yausername/youtubedl-android
         *
         * @Copyrights All credits goes to respective owners (developers).
         * */
        val dRequest = YoutubeDLRequest(dUrl)

        tmpFile.delete()
        tmpFile.mkdir()
        tmpFile.deleteOnExit()

        dRequest.addOption("--fragment-retries", "3")
        dRequest.addOption(
            "--user-agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
        )
        dRequest.addOption("--downloader", "libaria2c.so")
        dRequest.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
        dRequest.addOption("-f", "bv+ba/b") // Automatically takes best quality of video and audio
        dRequest.addOption("-o", "${tmpFile.absolutePath}/%(title)s.%(ext)s")

        try {
            YoutubeDL.getInstance()
                .execute(dRequest, dTaskId) { progress, _, line ->
                    val lineText = if (line.contains("[generic]"))
                        "Preparing"
                    else if (line.contains("[info]"))
                        "Extracting info"
                    else if (line.contains("DL:"))
                        line // "Downloading"
                    else if (line.contains("[Merger]"))
                        "Merging"
                    else if (line.contains("Destination"))
                        "∞"
                    else "∞"

                    downloaderService.updateDownloadProgress(
                        downloadTaskId = dTaskId,
                        progress = progress.toInt(),
                        progressText = if (progress.toInt() == -1) "∞" else "${progress.toInt()}%",
                        bigText = lineText
                    )
                }
            tmpFile.merge()
            downloaderService.downloadEndNotification(
                downloadTaskId = dTaskId,
                fileName = dFName, // tmpFile.name.substringBeforeLast(".")
                status = "Download successful.",
                reason = null,
                color = context.toColor(color = R.color.colorSecondaryDark),
                icon = android.R.drawable.stat_sys_download_done
            )
            Log.d(TAG, "DOWNLOAD DONE! [${dFName}]")
        } catch (e: Exception) {
            if (isCancelled) {
                downloaderService.downloadEndNotification(
                    downloadTaskId = dTaskId,
                    fileName = dFName,
                    status = "Download cancelled.",
                    reason = null,
                    color = context.toColor(color = android.R.color.holo_red_dark),
                    icon = android.R.drawable.stat_sys_download_done
                )
            } else {
                val reason = if (e.localizedMessage!!.contains(HTTP_ERROR_403)){
                    "Download failed. This video is not downloadable or download disabled by file author."
                }else {
                    "Download failed. Please push an issue to the developer for further information."
                }
                downloaderService.downloadEndNotification(
                    downloadTaskId = dTaskId,
                    fileName = dFName,
                    status = "Download failed.",
                    reason = reason,
                    color = context.toColor(color = android.R.color.holo_red_dark),
                    icon = android.R.drawable.stat_sys_download_done
                )
            }
        } finally {
            tmpFile.deleteRecursively()
        }
    }

    fun cancel() {
        isCancelled = true
        Log.e(TAG, "CANCELLING!... [TASK_ID] -> $dTaskId")
        YoutubeDL.getInstance().destroyProcessById(dTaskId)
    }

    private fun File.merge() {
        listFiles()!!.forEach {
            it.copyTo(
                File(dFPath, "${dFName}.${it.name.substringAfterLast(".")}"),
                true,
                BUFFER_SIZE
            )
        }
    }
}