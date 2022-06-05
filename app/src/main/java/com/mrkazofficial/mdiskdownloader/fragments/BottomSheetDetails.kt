package com.mrkazofficial.mdiskdownloader.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.models.VideoDataModel
import com.mrkazofficial.mdiskdownloader.tasks.DownloadManager
import com.mrkazofficial.mdiskdownloader.utils.Constants
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.grantPermissions
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.isPermissionGranted
import com.mrkazofficial.mdiskdownloader.utils.Utils
import com.mrkazofficial.mdiskdownloader.utils.Utils.formatDurationInt
import com.mrkazofficial.mdiskdownloader.utils.Utils.formatToHumanReadable
import com.mrkazofficial.mdiskdownloader.utils.Utils.replaceIllegalCharacters
import kotlinx.coroutines.launch
import java.io.File

/**
 * @Project mDisk Downloader
 * @Class BottomSheetDetails
 * @Author MRKaZ
 * @Since 11:41 AM, 5/30/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

class BottomSheetDetails : BottomSheetDialogFragment() {

    private lateinit var mActivity: Activity
    var videoDataModel: VideoDataModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            (view?.parent as View).setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), android.R.color.transparent
                )
            )
        }
        return dialog
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_details, container, false)

        setStyle(STYLE_NO_TITLE, R.style.CustomBottomSheetDialog)

        val imgThumb = view.findViewById<ImageView>(R.id.imgThumb)
        val txtMainTitle = view.findViewById<TextView>(R.id.txtMainTitle)
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtMainSize = view.findViewById<TextView>(R.id.txtMainSize)
        val txtSize = view.findViewById<TextView>(R.id.txtSize)
        val txtMainDuration = view.findViewById<TextView>(R.id.txtMainDuration)
        val txtDuration = view.findViewById<TextView>(R.id.txtDuration)
        val txtResolution = view.findViewById<TextView>(R.id.txtResolution)
        val txtAuthor = view.findViewById<TextView>(R.id.txtAuthor)

        val pbLoader = view.findViewById<ProgressBar>(R.id.pbLoader)
        pbLoader.apply {
            visibility = View.VISIBLE
            isIndeterminate = true
        }

        videoDataModel?.let {
            // It takes so much time to load
            //Glide.with(mActivity)
            //    .asBitmap()
            //    .diskCacheStrategy(DiskCacheStrategy.DATA)
            //    .load(it.url)
            //    .into(imgThumb)

            // Programmatically load
            lifecycleScope.launch {
                val thumbBitmap = Utils.retrieveVideoFrameFromVideo(url = it.url)
                imgThumb.setImageBitmap(thumbBitmap)
            }

            txtMainTitle.text = it.fileName
            txtTitle.text = it.fileName

            runCatching {
                txtMainSize.text = it.fileSize.formatToHumanReadable
                txtSize.text = it.fileSize.formatToHumanReadable
            }

            txtMainDuration.text = it.duration.formatDurationInt(context = requireContext())
            txtDuration.text = it.duration.formatDurationInt(context = requireContext())

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
                val mimeTypeMap = ".${it.url.substringAfterLast(".")}"
                val fileName = if (it.fileName.contains(mimeTypeMap)) {
                    it.fileName
                } else "${it.fileName}$mimeTypeMap"
                if (mActivity.isPermissionGranted()) {
                    /** If the permission already grant start the downloading task. */
                    val downloadFolder = File("${Constants.DOWNLOADS_FOLDER}/mDisk Downloader")
                    /** Folder checks */
                    if (!downloadFolder.exists()) downloadFolder.mkdirs()
                    val fileCheck =
                        File("${Constants.DOWNLOADS_FOLDER}/mDisk Downloader", fileName)
                    if (!fileCheck.exists()) {
                        DownloadManager.downloadFile(
                            context = mActivity,
                            url = it.url,
                            fileName = fileName.replaceIllegalCharacters,
                            filePath = downloadFolder.absolutePath
                        )
                        Toast.makeText(
                            mActivity,
                            "${requireContext().getString(R.string.download_started_toast)}$fileName",
                            Toast.LENGTH_SHORT
                        ).show()
                        this.dismiss()
                    } else Toast.makeText(
                        mActivity,
                        "${requireContext().getString(R.string.file_already_exists_toast)}$fileName",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // If not permission allowed so ask for permission from user to perform download task
                    Toast.makeText(
                        mActivity,
                        requireContext().getString(R.string.please_allow_permission_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                    mActivity.grantPermissions()
                }
            }

            view.findViewById<MaterialButton>(R.id.btnCopyLink).setOnClickListener { _ ->
                Utils.copyToClipboard(context = mActivity, it.url)
                Toast.makeText(
                    mActivity,
                    requireContext().getString(R.string.link_copied_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return view
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