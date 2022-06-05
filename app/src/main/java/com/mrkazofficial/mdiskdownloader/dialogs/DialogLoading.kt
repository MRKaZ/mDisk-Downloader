package com.mrkazofficial.mdiskdownloader.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.mrkazofficial.mdiskdownloader.R

/**
 * @Project mDisk Downloader
 * @Class DialogLoading
 * @Author MRKaZ
 * @Since 11:41 AM, 5/30/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

@Suppress("DEPRECATION")
class DialogLoading
@SuppressLint("InflateParams")
constructor(private val mContext: Context) {
    private val mDialog: Dialog
    fun show() {
        (mContext as AppCompatActivity).runOnUiThread { mDialog.show() }
    }

    fun dismiss() {
        (mContext as AppCompatActivity).runOnUiThread { mDialog.dismiss() }
    }

    fun setCancelable(setCancelable: Boolean) {
        mDialog.setCancelable(setCancelable)
    }

    fun setMessage(mTitle: String?) {
        val setMessage = mDialog.findViewById<TextView>(R.id.dialogMessage)
        if (setMessage != null) {
            setMessage.visibility = View.VISIBLE
            setMessage.text = mTitle
        }
    }

    init {
        val mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(displayMetrics)
        val mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null)
        val screenWidth = displayMetrics.widthPixels
        // screenHeight = displayMetrics.heightPixels;
        mView.minimumWidth = screenWidth
        mDialog = Dialog(mContext, R.style.CustomDialogTheme)
        mDialog.requestWindowFeature(1) // FEATURE_NO_TITLE = 1
        mDialog.setContentView(mView)
        mDialog.window!!.setGravity(Gravity.CENTER) // Gravity.CENTER = int 17
        val mWindowManagerLayout = mDialog.window!!
            .attributes
        mWindowManagerLayout.x = 0
        mWindowManagerLayout.y = 0
        mDialog.window!!.attributes = mWindowManagerLayout

        val progressBar = mDialog.findViewById(R.id.progressSpinner) as CircularProgressIndicator

        progressBar.indeterminateDrawable?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    mContext,
                    R.color.colorSecondary
                ), BlendModeCompat.SRC_ATOP
            )
    }
}