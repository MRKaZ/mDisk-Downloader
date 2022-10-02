package com.mrkazofficial.mdiskdownloader.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.models.VideoDataModel
import com.mrkazofficial.mdiskdownloader.services.DownloaderService
import com.mrkazofficial.mdiskdownloader.tasks.DownloadManager
import com.mrkazofficial.mdiskdownloader.utils.Constants
import com.mrkazofficial.mdiskdownloader.utils.Constants.ADD_DOWNLOAD_MESSENGER_TASK
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOADER_FILENAME_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOADER_PATH_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.DOWNLOADER_URL_ARG
import com.mrkazofficial.mdiskdownloader.utils.Constants.MESSENGER_TASK
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.grantPermissions
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.isPermissionGranted
import com.mrkazofficial.mdiskdownloader.utils.Utils
import com.mrkazofficial.mdiskdownloader.utils.Utils.formatDurationInt
import com.mrkazofficial.mdiskdownloader.utils.Utils.formatToHumanReadable
import com.mrkazofficial.mdiskdownloader.utils.Utils.replaceIllegalCharacters
import java.io.File

/**
 * @Project mDisk Downloader
 * @Class BottomSheetDetails
 * @Author MRKaZ
 * @Since 11:41 AM, 5/30/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */
private const val TAG = "BottomSheetDetails"

class BottomSheetDetails : BottomSheetDialogFragment() {

    private lateinit var mActivity: Activity
    var videoDataModel: VideoDataModel? = null
    private var sMessenger: Messenger? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            (view?.parent as View).setBackgroundColor(
                ContextCompat.getColor(
                    mActivity, android.R.color.transparent
                )
            )
        }
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Bind `DownloaderService` with `BottomSheetDetails` DialogFragment
        mActivity.bindService(
            Intent(context, DownloaderService::class.java),
            DServiceConnection(),
            0
        )
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_details, container, false)

        setStyle(STYLE_NO_TITLE, R.style.CustomBottomSheetDialog)


        val txtMainTitle = view.findViewById<TextView>(R.id.txtMainTitle)
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtMainSize = view.findViewById<TextView>(R.id.txtMainSize)
        val txtSize = view.findViewById<TextView>(R.id.txtSize)
        val txtMainDuration = view.findViewById<TextView>(R.id.txtMainDuration)
        val txtDuration = view.findViewById<TextView>(R.id.txtDuration)
        val txtResolution = view.findViewById<TextView>(R.id.txtResolution)
        val txtAuthor = view.findViewById<TextView>(R.id.txtAuthor)

        val lLayoutNo = view.findViewById<LinearLayout>(R.id.lLayoutNo)
        lLayoutNo.apply {
            visibility = View.GONE
        }

        videoDataModel?.let {
            lLayoutNo.visibility = if (it.source.endsWith(".mpd")) View.GONE else View.VISIBLE

            txtMainTitle.text = it.fileName
            txtTitle.text = it.fileName

            runCatching {
                txtMainSize.text = it.fileSize.formatToHumanReadable
                txtSize.text = it.fileSize.formatToHumanReadable
            }

            txtMainDuration.text = it.duration.formatDurationInt(context = mActivity)
            txtDuration.text = it.duration.formatDurationInt(context = mActivity)

            txtResolution.text = "${it.width}x${it.height}"
            txtAuthor.text =
                if (it.sourceType == "telegram_bot") "@${it.uploaderName}" else it.uploaderName

            if (it.sourceType == "telegram_bot") {
                txtAuthor.setOnClickListener { _ ->
                    val intent =
                        Utils.openFromViaTelegram(context = mActivity, userId = it.uploaderName)
                    startActivity(intent)
                }
            }

            view.findViewById<MaterialButton>(R.id.btnDownload).setOnClickListener { _ ->
                if (mActivity.isPermissionGranted()) {
                    /** If the permission already grant start the downloading task. */
                    val downloadFolder = File("${Constants.DOWNLOADS_FOLDER}/mDisk Downloader")
                    /** Folder checks */
                    if (!downloadFolder.exists()) downloadFolder.mkdirs()
                    val fileCheck =
                        File(
                            "${Constants.DOWNLOADS_FOLDER}/mDisk Downloader",
                            it.fileName
                        )
                    if (!fileCheck.exists()) {
                        if (it.source.endsWith(".mpd")) {
                            requestDownload(
                                url = it.source,
                                fileName = it.fileName,
                                downloadPath = downloadFolder.absolutePath
                            )
                            Toast.makeText(
                                mActivity,
                                "${mActivity.getString(R.string.download_started_toast)}${it.fileName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            MaterialAlertDialogBuilder(mActivity, R.style.DrmAlertDialog)
                                .setTitle("Alert")
                                .setMessage("According to file data. This file cannot bypass DRM protection. Would you like to continue downloading?")
                                .setNegativeButton("Continue") { _, _ ->
                                    DownloadManager.downloadFile(
                                        context = mActivity,
                                        url = it.url,
                                        fileName = it.fileName.replaceIllegalCharacters,
                                        filePath = downloadFolder.absolutePath
                                    )
                                    Toast.makeText(
                                        mActivity,
                                        "${mActivity.getString(R.string.download_started_toast)}${it.fileName}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .setPositiveButton("Cancel") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                        }
                        this.dismiss()
                    } else Toast.makeText(
                        mActivity,
                        "${mActivity.getString(R.string.file_already_exists_toast)}${it.fileName}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If not permission allowed so ask for runtime permission from user to perform download task
                    Toast.makeText(
                        mActivity,
                        mActivity.getString(R.string.please_allow_permission_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                    mActivity.grantPermissions()
                }
            }

            view.findViewById<MaterialButton>(R.id.btnCopyLink).setOnClickListener { _ ->
                Utils.copyToClipboard(context = mActivity, it.url)
                Toast.makeText(
                    mActivity,
                    mActivity.getString(R.string.link_copied_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return view
    }

    private fun requestDownload(url: String, fileName: String, downloadPath: String) {
        sMessenger?.send(Message().apply {
            data.putString(MESSENGER_TASK, ADD_DOWNLOAD_MESSENGER_TASK)
            data.putString(DOWNLOADER_URL_ARG, url)
            data.putString(DOWNLOADER_FILENAME_ARG, fileName)
            data.putString(DOWNLOADER_PATH_ARG, downloadPath)
        })
    }

    private inner class DServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            sMessenger = Messenger(service)
            Log.d(TAG, "Downloader Service Connection Connected!")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sMessenger = null
            mActivity.unbindService(this@DServiceConnection)
            Log.d(TAG, "Downloader Service Connection Disconnected!")
        }
    }

    override fun onAttach(context: Context) {
        if (context is Activity) {
            mActivity = context
        }
        super.onAttach(context)
    }

    /** Set background color TRANSPARENT to add a custom background (rounded) */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}