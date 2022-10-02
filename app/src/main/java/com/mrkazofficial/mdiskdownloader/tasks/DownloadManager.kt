package com.mrkazofficial.mdiskdownloader.tasks

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.text.format.Formatter
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.utils.Constants
import com.mrkazofficial.mdiskdownloader.utils.executeAsyncTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File


/**
 *
 * @Project mDisk Downloader
 * @Class DownloadManager
 * @Author MRKaZ
 * @Since 12:32 PM, 5/29/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

@Suppress("DEPRECATION")
object DownloadManager {

    fun registerReceiver(context: Context) {
        context.registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    @MainThread
    @SuppressLint("ObsoleteSdkInt", "Range")
    fun downloadFile(context: Context, url: String, fileName: String, filePath: String) {
        val file = File(filePath, fileName)
        var request: DownloadManager.Request? = null
        CoroutineScope(Dispatchers.IO).executeAsyncTask({
            /** onPreExecute */

            request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(fileName)
                    .setDescription(context.getString(R.string.downloading))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    /** OnePlus (1+) Devices doesn't support
                     * setAllowedNetworkTypes Disable due some new OnePlus (1+) devices does not downloading files.
                     * It'll automatically get pause. Asking for WIFI a network to download file.
                     */
                    //.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI and DownloadManager.Request.NETWORK_MOBILE)
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
            } else {
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(fileName)
                    .setDescription(context.getString(R.string.downloading))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    /** OnePlus (1+) Devices doesn't support
                     * setAllowedNetworkTypes Disable due some new OnePlus (1+) devices does not downloading files.
                     * It'll automatically get pause. Asking for WIFI a network to download file.
                     */
                    //.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI and DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
            }
        }, {
            /** doInBackground */
            runCatching {
                val downloadManager =
                    context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
                val downloadId = downloadManager.enqueue(request)
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }
                    cursor.close()
                }
            }
        }, {
            /** onPostExecute */
        })
    }

    /** Status */
    private val Cursor.getStatus: String
        @SuppressLint("Range")
        get() = when (this.getInt(this.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            DownloadManager.STATUS_FAILED -> "Failed"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_RUNNING -> "Running"
            DownloadManager.STATUS_SUCCESSFUL -> "Completed"
            DownloadManager.STATUS_PENDING -> "Pending"
            else -> "Unknown"
        }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.let {
                runCatching {
                    //retrieving the file
                    val downloadedFileId = it.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                    val dQuery = DownloadManager.Query()
                    dQuery.setFilterById(downloadedFileId)
                    val dCursor: Cursor = downloadManager.query(dQuery)

                    if (dCursor.moveToFirst()) {
                        val status =
                            dCursor.getInt(dCursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        val fileName =
                            dCursor.getString(dCursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                        val uriString: String =
                            dCursor.getString(dCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                context.sendBroadcastListChanged()
                                Toast.makeText(
                                    context,
                                    "${context.getString(R.string.dm_complete_toast)}$fileName",
                                    Toast.LENGTH_SHORT
                                ).show()
                                scanFiles(context = context, filePath = uriString)
                            }
                            DownloadManager.STATUS_FAILED -> {
                                Toast.makeText(
                                    context,
                                    "${context.getString(R.string.dm_failed_toast)}$fileName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scanFiles(context: Context, filePath: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            mediaScanIntent.data = Uri.parse(filePath)
            context.sendBroadcast(mediaScanIntent)
        }
    }

    /**
     * Sending broadcast to DownloadsFragment
     * @Check DownloadsFragment fragment to registered broadcast receiver
     * @Because download list has been changed
     * */
    private fun Context.sendBroadcastListChanged() {
        val intent = Intent(/*Put action here or down below*/)
        intent.action = Constants.LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY
        intent.putExtra(Constants.LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY, true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

}