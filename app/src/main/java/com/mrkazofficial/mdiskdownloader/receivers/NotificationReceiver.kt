package com.mrkazofficial.mdiskdownloader.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mrkazofficial.mdiskdownloader.services.DownloaderService
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOAD_NOTIFICATION_TASK_ID_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.LOCAL_BROADCAST_SERVICE_WORKER
import com.mrkazofficial.mdiskdownloader.utils.Constants.MESSENGER_TASK
import com.mrkazofficial.mdiskdownloader.utils.Constants.SERVICE_ACTION_SHUTDOWN_ARG

/**
 * @Project mDisk Downloader
 * @Class NotificationReceiver
 * @Author MRKaZ
 * @Since 3:28 AM, 9/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG) {
                val taskId = it.getStringExtra(DOWNLOAD_NOTIFICATION_TASK_ID_ARG)
                if (taskId != null) {
                    sendBroadcast(context = context!!, taskId = taskId)
                }
            }else if (it.action == SERVICE_ACTION_SHUTDOWN_ARG){
                sendBroadcast(context = context!!, taskId = null)
                Intent(context, DownloaderService::class.java).also { service ->
                    context.stopService(service)
                }
            }
        }
    }

    private fun sendBroadcast(context: Context, taskId: String?) {
        val intent = Intent(LOCAL_BROADCAST_SERVICE_WORKER)
        if (taskId != null) {
            intent.putExtra(MESSENGER_TASK, DOWNLOAD_NOTIFICATION_ACTION_CANCEL_ARG)
            intent.putExtra(DOWNLOAD_NOTIFICATION_TASK_ID_ARG, taskId)
        } else {
            intent.putExtra(MESSENGER_TASK, SERVICE_ACTION_SHUTDOWN_ARG)
        }

        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(intent)
    }
}