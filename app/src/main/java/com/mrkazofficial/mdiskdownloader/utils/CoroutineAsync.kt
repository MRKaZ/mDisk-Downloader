package com.mrkazofficial.mdiskdownloader.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Project mDisk Downloader
 * @Class CoroutineAsync
 * @Author MRKaZ
 * @Since 6:32 PM, 5/26/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

fun <R> CoroutineScope.executeAsyncTask(
    onPreExecute: () -> Unit,
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit
) = launch {
    onPreExecute()
    val result = withContext(Dispatchers.IO) {
        doInBackground()
    }
    onPostExecute(result)
}