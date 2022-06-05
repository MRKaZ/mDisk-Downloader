package com.mrkazofficial.mdiskdownloader.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.utils.Constants.LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY
import com.mrkazofficial.mdiskdownloader.utils.Constants.LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.grantPermissions
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.isPermissionGranted
import com.mrkazofficial.mdiskdownloader.utils.Utils.formatDurationLong
import com.mrkazofficial.mdiskdownloader.utils.Utils.formatToHumanReadable
import com.mrkazofficial.mdiskdownloader.utils.Utils.isInstalled
import com.mrkazofficial.mdiskdownloader.utils.Utils.retrieveFileDuration
import java.io.File


/**
 * @Project mDisk Downloader
 * @Class DownloadListAdapter
 * @Author MRKaZ
 * @Since 8:17 PM, 5/31/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

private const val TAG = "DownloadListAdapter"

@SuppressLint("NotifyDataSetChanged")
internal class DownloadListAdapter :
    RecyclerView.Adapter<DownloadListAdapter.DownloadListAdapterHolder>() {

    private lateinit var mContext: Context
    private var fileList: ArrayList<File> = ArrayList()

    fun setupList(fileList: ArrayList<File>) {
        this.fileList = fileList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadListAdapterHolder {
        this.mContext = parent.context
        return DownloadListAdapterHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.download_list_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DownloadListAdapterHolder, position: Int) {

        holder.txtMainTitle.text = null
        holder.txtMainDuration.text = null
        holder.txtMainSize.text = null

        this.fileList[position].name.let {
            holder.txtMainTitle.text = it
        }

        this.fileList[position].absolutePath.let {
            Glide.with(mContext)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .load(it)
                .into(holder.imgThumb)
        }

        this.fileList[position].let {
            val fileDuration = it.retrieveFileDuration(context = mContext)
            holder.txtMainDuration.text = fileDuration.formatDurationLong(context = mContext)

            runCatching {
                holder.txtMainSize.text = it.length().formatToHumanReadable
            }
        }

        holder.layoutContainer.setOnClickListener {
            val items = arrayOf(
                mContext.getString(R.string.dialog_play),
                mContext.getString(R.string.dialog_delete)
            )

            MaterialAlertDialogBuilder(mContext)
                .setTitle(mContext.getString(R.string.dialog_choose))
                .setItems(items) { _, which ->
                    when (items[which]) {
                        mContext.getString(R.string.dialog_play) -> {
                            openMXPlayer(position = position)
                        }
                        mContext.getString(R.string.dialog_delete) -> {
                            try {
                                if (mContext.isPermissionGranted()) {
                                    val file = this.fileList[position]

                                    if (deleteFile(mContext, file = this.fileList[position]))
                                        Toast.makeText(
                                            mContext,
                                            "${file.name} ${mContext.getString(R.string.deleted_toast)}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    else Toast.makeText(
                                        mContext,
                                        mContext.getString(R.string.delete_failed_toast),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Sending broadcast message to update download files list
                                    sendBroadcastListChanged()

                                    // Remove specific file from the list
                                    this.fileList.toMutableList().remove(this.fileList[position])
                                    // Removing item from the list
                                    this.fileList.toMutableList().removeAt(position)

                                    // Notifying remove's and change's
                                    notifyItemRemoved(position)
                                    notifyItemRangeRemoved(position, this.fileList.size)
                                    notifyDataSetChanged()
                                } else {
                                    mContext.grantPermissions()
                                    Toast.makeText(
                                        mContext,
                                        mContext.getString(R.string.permission_need_message_adapter),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.delete_failed_toast),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                .setCancelable(true)
                .create()
                .show()
        }
    }

    override fun getItemCount(): Int {
        return this.fileList.size
    }

    internal inner class DownloadListAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMainTitle: TextView = view.findViewById(R.id.txtMainTitle)
        val txtMainSize: TextView = view.findViewById(R.id.txtMainSize)
        val txtMainDuration: TextView = view.findViewById(R.id.txtMainDuration)
        val layoutContainer: CardView = view.findViewById(R.id.layoutContainer)

        val imgThumb: ImageView = view.findViewById(R.id.imgThumb)
    }

    private fun openMXPlayer(position: Int) {
        /** MX Player Package's
         * com.mxtech.videoplayer.pro - Pro Edition
         * com.mxtech.videoplayer.ad - Free Edition
         */
        val intentMxPro = Intent(Intent.ACTION_VIEW)
        intentMxPro.setDataAndType(
            Uri.parse(this.fileList[position].absolutePath),
            "video/*"
        )
        intentMxPro.putExtra("decode_mode", "HW decoder")
        intentMxPro.setPackage("com.mxtech.videoplayer.pro")

        val intentMxAd = Intent(Intent.ACTION_VIEW)
        intentMxAd.setDataAndType(
            Uri.parse(this.fileList[position].absolutePath),
            "video/*"
        )
        intentMxAd.putExtra("decode_mode", "HW decoder")
        intentMxAd.setPackage("com.mxtech.videoplayer.ad")

        if (mContext.isInstalled(intent = intentMxPro)) {
            mContext.startActivity(intentMxPro)
        } else if (mContext.isInstalled(intent = intentMxAd)) {
            mContext.startActivity(intentMxAd)
        } else Toast.makeText(
            mContext,
            mContext.getString(R.string.mx_player_install_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteFile(context: Context, file: File): Boolean {
        val where = MediaStore.MediaColumns.DATA + "=?"
        val selectionArgs = arrayOf(
            file.absolutePath
        )
        val contentResolver = context.contentResolver
        val filesUri = MediaStore.Files.getContentUri("external")
        contentResolver.delete(filesUri, where, selectionArgs)
        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs)
        }
        return !file.exists()
    }

    /**
     * Sending broadcast to DownloadsFragment
     * @Check DownloadsFragment fragment to registered broadcast receiver
     * @Because download list has been changed
     * */
    private fun sendBroadcastListChanged() {
        val intent = Intent(/*Put action here or down below*/)
        intent.action = LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY
        intent.putExtra(LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY, true)
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
    }

}