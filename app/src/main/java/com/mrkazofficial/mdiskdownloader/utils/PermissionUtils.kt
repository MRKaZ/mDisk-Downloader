package com.mrkazofficial.mdiskdownloader.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mrkazofficial.mdiskdownloader.R
import com.permissionx.guolindev.PermissionX

/**
 * @Project mDisk Downloader
 * @Class PermissionUtils
 * @Author MRKaZ
 * @Since 10:58 PM, 5/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

object PermissionUtils {
    private const val TAG = "PermissionUtils"

    fun Context.grantPermissions() {
        isPermissionGranted().let {
            if (!it) {
                PermissionX.init((this as AppCompatActivity))
                    .permissions(
                        mutableListOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    .explainReasonBeforeRequest()
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            this.getString(R.string.permission_utils_main_message),
                            this.getString(R.string.permission_utils_dialog_ok),
                            this.getString(R.string.permission_utils_dialog_cancel)
                        )
                    }
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            this.getString(R.string.permission_utils_message),
                            this.getString(R.string.permission_utils_dialog_ok),
                            this.getString(R.string.permission_utils_dialog_cancel)
                        )
                    }.request { allGranted, _, deniedList ->
                        if (allGranted) {
                            Log.e(TAG, "All permissions are granted")
                        } else {
                            Toast.makeText(
                                this,
                                "${this.getString(R.string.permission_utils_denied_message)}$deniedList",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

    }

    fun Context.isPermissionGranted(): Boolean {
        val readExternalStorageResults = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeExternalStorageResults = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return readExternalStorageResults == PackageManager.PERMISSION_GRANTED && writeExternalStorageResults == PackageManager.PERMISSION_GRANTED
    }
}