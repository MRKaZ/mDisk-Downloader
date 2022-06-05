package com.mrkazofficial.mdiskdownloader.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkazofficial.mdiskdownloader.di.RetroClient
import com.mrkazofficial.mdiskdownloader.models.VideoDataModel
import kotlinx.coroutines.Dispatchers
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

    private suspend fun doLoadVideoData(videoId: String) {
        val call = RetroClient.instance.getVideoData(videoId = videoId)
        val asynchronous = withContext(Dispatchers.Default) { call }
        val awaitResponse = asynchronous.awaitResponse()

        awaitResponse.runCatching {
            if (this.isSuccessful && this.code() == 200) {
                this.body().let { body ->
                    if (body != null) {
                        mutableLiveData.postValue(body)
                    } else mutableLiveData.postValue(null)
                }
            } else mutableLiveData.postValue(null)
        }
    }
}