package com.mrkazofficial.mdiskdownloader.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkazofficial.mdiskdownloader.di.RetroClient
import com.mrkazofficial.mdiskdownloader.models.VideoDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

/**
 * @Project mDisk Downloader
 * @Class VideoDataViewModel
 * @Author MRKaZ
 * @Since 10:24 PM, 5/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

class VideoDataViewModel : ViewModel() {

    private val mutableLiveData: SingleLiveEvent<VideoDataModel?> by lazy {
        SingleLiveEvent()
    }

    fun getVideoData(videoId: String) {
        with(viewModelScope) {
            this.launch {
                mutableLiveData.postValue(null)
                doLoadVideoData(videoId = videoId)
            }
        }
    }

    val videoData: MutableLiveData<VideoDataModel?> = mutableLiveData

    fun doLoadVideoDataAsFlow(videoId: String) = callbackFlow {
        withContext(Dispatchers.IO) { RetroClient.instance.getVideoData(videoId = videoId) }
            .awaitResponse()
            .runCatching {
                /** If the data == null
                 * You-are trying to download NON-DOWNLOADABLE video file or Sharing cancelled by the file author.
                 */
                //Log.e("TAG", this.errorBody()?.string()!!)
                if (this.isSuccessful && this.code() == 200) {
                    this.body().let { body ->
                        if (body != null) {
                            send(body)
                        } else send(null)
                    }
                } else {
                    send(null)
                    cancel(
                        if (this.errorBody()
                                ?.string()!! == "Sharing cancelled"
                        ) "Sharing cancelled" else this.errorBody()!!.string()
                    )
                }
            }
        awaitClose()
    }

    private suspend fun doLoadVideoData(videoId: String) {
        withContext(Dispatchers.IO) { RetroClient.instance.getVideoData(videoId = videoId) }
            .awaitResponse()
            .runCatching {
                /** If the data == null
                 * You-are trying to download NON-DOWNLOADABLE video file or Sharing cancelled by the file author.
                 */
                //Log.e("TAG", this.errorBody()?.string()!!)
                if (this.isSuccessful && this.code() == 200) {
                    this.body().let { body ->
                        if (body != null) {
                            mutableLiveData.postValue(body)
                        } else mutableLiveData.postValue(null)
                    }
                } else {
                    if (this.errorBody()?.string()!! == "Sharing cancelled") {
                        Log.e("", "Sharing cancelled")
                    }
                    mutableLiveData.postValue(null)
                }
            }
    }
}