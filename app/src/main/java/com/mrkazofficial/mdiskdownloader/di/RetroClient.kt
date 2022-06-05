package com.mrkazofficial.mdiskdownloader.di

import com.mrkazofficial.mdiskdownloader.networking.RetroInterface
import com.mrkazofficial.mdiskdownloader.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Project mDisk Downloader
 * @Class RetroClient
 * @Author MRKaZ
 * @Since 9:37 PM, 5/24/2022
 * @Origin Taprobana (LK)
 * @Copyright (c) 2022 MRKaZ. All rights reserved.
 */

object RetroClient {

    private val client = OkHttpClient.Builder()
        .callTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: RetroInterface = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetroInterface::class.java)
}