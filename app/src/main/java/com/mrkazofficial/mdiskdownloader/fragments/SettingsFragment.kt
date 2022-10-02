package com.mrkazofficial.mdiskdownloader.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mrkazofficial.mdiskdownloader.BuildConfig
import com.mrkazofficial.mdiskdownloader.R
import com.mrkazofficial.mdiskdownloader.utils.Constants
import com.mrkazofficial.mdiskdownloader.utils.Utils.openGithubProfile
import com.mrkazofficial.mdiskdownloader.utils.Utils.openGithubSource


class SettingsFragment : Fragment() {

    private lateinit var mActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val txtDownloads = view.findViewById<TextView>(R.id.txtDownloads)
        val txtAppVersion = view.findViewById<TextView>(R.id.txtAppVersion)
        val txtAppPackage = view.findViewById<TextView>(R.id.txtAppPackage)

        val appDirectory = "${Constants.DOWNLOADS_FOLDER}/mDisk Downloader"
        val appVersion = "v" + BuildConfig.VERSION_NAME
        val appPackage: String = BuildConfig.APPLICATION_ID

        txtDownloads.text = appDirectory
        txtAppVersion.text = appVersion
        txtAppPackage.text = appPackage

        view.findViewById<LinearLayout>(R.id.layoutAuthorGithub).setOnClickListener {
            mActivity.openGithubProfile()
        }

        view.findViewById<LinearLayout>(R.id.layoutSourceGithub).setOnClickListener {
            mActivity.openGithubSource()
        }

        return view
    }

    override fun onAttach(context: Context) {
        if (context is Activity)
            this.mActivity = context
        super.onAttach(context)
    }
}