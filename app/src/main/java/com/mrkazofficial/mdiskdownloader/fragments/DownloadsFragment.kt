package com.mrkazofficial.mdiskdownloader.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.adapters.DownloadListAdapter
import com.mrkazofficial.mdiskdownloader.utils.Constants
import com.mrkazofficial.mdiskdownloader.utils.Constants.LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY
import com.mrkazofficial.mdiskdownloader.utils.Constants.LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.grantPermissions
import com.mrkazofficial.mdiskdownloader.utils.PermissionUtils.isPermissionGranted
import com.mrkazofficial.mdiskdownloader.utils.Utils.toColor
import kotlinx.coroutines.launch
import java.io.File


@SuppressLint("NotifyDataSetChanged")
class DownloadsFragment : Fragment() {

    private lateinit var mActivity: Activity

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var downloadListAdapter: DownloadListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_downloads, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        val rvDownloadList = view.findViewById<RecyclerView>(R.id.rvDownloadList)

        layoutEmpty.apply {
            visibility = View.GONE
        }

        swipeRefreshLayout.apply {
            setColorSchemeColors(mActivity.toColor(color = R.color.colorSecondaryLight))
        }

        rvDownloadList.apply {
            layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        }

        val downloadFiles = File("${Constants.DOWNLOADS_FOLDER}/mDisk Downloader").downloadedFiles
        downloadListAdapter = DownloadListAdapter()

        if (mActivity.isPermissionGranted()) {
            lifecycleScope.launch {
                if (downloadFiles != null) {
                    if (downloadFiles.isNotEmpty()) {
                        downloadListAdapter.setupList(fileList = downloadFiles)
                        mActivity.runOnUiThread {
                            downloadListAdapter.notifyDataSetChanged()
                        }
                        layoutEmpty.visibility = View.GONE
                        swipeRefreshLayout.visibility = View.VISIBLE
                    } else {
                        layoutEmpty.visibility = View.VISIBLE
                        swipeRefreshLayout.visibility = View.GONE
                    }
                } else {
                    layoutEmpty.visibility = View.VISIBLE
                    swipeRefreshLayout.visibility = View.GONE
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                Handler(Looper.getMainLooper()).postDelayed({
                    swipeRefreshLayout.isRefreshing = false
                    mActivity.runOnUiThread {
                        downloadListAdapter.notifyItemInserted(-1)
                        downloadListAdapter.notifyDataSetChanged()
                    }
                    Toast.makeText(
                        mActivity,
                        getString(R.string.refreshed_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }, 1500) // 1.5 Secs
            }

            rvDownloadList.adapter = downloadListAdapter
        } else {
            mActivity.grantPermissions()
            Toast.makeText(
                mActivity,
                getString(R.string.permission_need_to_allow_access_storage),
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }


    val File.downloadedFiles: ArrayList<File>?
        get() {
            var fileList: ArrayList<File>? = ArrayList()
            val listAllFiles = this.listFiles()

            if (listAllFiles != null && listAllFiles.isNotEmpty()) {
                fileList?.addAll(listAllFiles)
            } else {
                fileList = null
            }
            return fileList
        }

    /** Register receivers */
    private fun Context.registerReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            fileListChangedBroadcastReceiver,
            IntentFilter(LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY)
        )
    }

    /** Unregister receivers */
    private fun Context.unRegisterReceivers() {
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(fileListChangedBroadcastReceiver)
    }

    /** List changing broadcast receiver */
    private var fileListChangedBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action.let {
                if (it == LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_EVENT_KEY) {
                    val isListChanged = intent!!.getBooleanExtra(
                        LOCAL_BROADCAST_DOWNLOAD_FILES_LIST_CHANGED_KEY,
                        false
                    )
                    if (isListChanged) {
                        val downloadFiles =
                            File("${Constants.DOWNLOADS_FOLDER}/mDisk Downloader").downloadedFiles

                        lifecycleScope.launch {
                            if (downloadFiles != null) {
                                if (downloadFiles.isNotEmpty()) {
                                    downloadListAdapter.setupList(fileList = downloadFiles)
                                    mActivity.runOnUiThread {
                                        downloadListAdapter.notifyDataSetChanged()
                                    }
                                    layoutEmpty.visibility = View.GONE
                                    swipeRefreshLayout.visibility = View.VISIBLE
                                } else {
                                    layoutEmpty.visibility = View.VISIBLE
                                    swipeRefreshLayout.visibility = View.GONE
                                }
                            } else {
                                layoutEmpty.visibility = View.VISIBLE
                                swipeRefreshLayout.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        // Register receivers in here
        requireContext().registerReceivers()
        super.onStart()
    }

    override fun onStop() {
        // Unregister receivers in here
        requireContext().unRegisterReceivers()
        super.onStop()
    }

    override fun onDestroy() {
        // Unregister receivers also in here
        requireContext().unRegisterReceivers()
        super.onDestroy()
    }

    override fun onResume() {
        mActivity.runOnUiThread {
            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            downloadListAdapter.notifyDataSetChanged()
        }
        super.onResume()
    }

    override fun onAttach(context: Context) {
        if (context is Activity)
            this.mActivity = context
        super.onAttach(context)
    }
}