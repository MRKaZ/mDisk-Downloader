package com.mrkazofficial.mdiskdownloader.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideoDataModel(
    @Expose
    @SerializedName("filename")
    var fileName: String,
    @Expose
    @SerializedName("display_name")
    var uploaderName: String,
    @Expose
    @SerializedName("download")
    var url: String,
    @Expose
    @SerializedName("from")
    var uploaderId: String,
    @Expose
    @SerializedName("rid")
    var randomId: String,
    @Expose
    @SerializedName("source_type")
    var sourceType: String,
    @Expose
    @SerializedName("ts")
    var timeInSeconds: Long,
    @Expose
    @SerializedName("size")
    var fileSize: Long,

    var source: String,
    var duration: Int,
    var height: Int,
    var width: Int,
    var poster: String

)