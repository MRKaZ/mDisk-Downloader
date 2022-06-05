package com.mrkazofficial.mdiskdownloader.utils

import android.content.Context
import android.content.SharedPreferences
import com.mrkazofficial.mdiskdownloader.utils.Constants.INFO_SESSION_HIDE_KEY_SHARED_PREF
import com.mrkazofficial.mdiskdownloader.utils.Constants.INFO_SESSION_MAIN_KEY_SHARED_PREF

/**
 * @Project mDisk Downloader
 * @Class SharedPreferenceUtils
 * @Author MRKaZ
 * @Since 9:23 PM, 6/1/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

object SharedPreferenceUtils {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun initiate(context: Context) {
        sharedPreferences =
            context.getSharedPreferences(
                INFO_SESSION_MAIN_KEY_SHARED_PREF,
                Context.MODE_PRIVATE
            )

        editor = sharedPreferences.edit()
    }

    fun Boolean.save() {
        editor.putBoolean(INFO_SESSION_HIDE_KEY_SHARED_PREF, this)
        editor.commit()
        editor.apply()
    }

    val isHidden: Boolean
        get() = sharedPreferences.getBoolean(
            INFO_SESSION_HIDE_KEY_SHARED_PREF,
            false
        )
}