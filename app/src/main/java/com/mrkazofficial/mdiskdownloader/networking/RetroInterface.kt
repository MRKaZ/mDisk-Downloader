package com.mrkazofficial.mdiskdownloader.networking

import com.mrkazofficial.mdiskdownloader.models.VideoDataModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * @Project mDisk Downloader
 * @Class RetroInterface
 * @Author MRKaZ
 * @Since 9:42 PM, 5/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

sealed interface RetroInterface {

    @Headers(
        "Accept: application/json; charset=utf-8",
        "Cache-control: max-age=3600, no-store, no-cache, must-revalidate, post-check=0, pre-check=0",
        "Pragma: no-cache",
        "X-Requested-With: XMLHttpRequest"
    )
    @GET("v1/file/cdnurl")
    fun getVideoData(
        @Query("param") videoId: String
    ): Call<VideoDataModel>

}