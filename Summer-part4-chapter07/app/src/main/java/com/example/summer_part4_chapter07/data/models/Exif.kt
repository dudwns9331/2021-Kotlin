package com.example.summer_part4_chapter07.data.models


import com.google.gson.annotations.SerializedName

data class Exif(
    @SerializedName("aperture")
    val aperture: String?,
    @SerializedName("exposure_time")
    val exposureTime: String?,
    @SerializedName("focal_length")
    val focalLength: String?,
    @SerializedName("iso")
    val iso: Int?,
    @SerializedName("make")
    val make: String?,
    @SerializedName("model")
    val model: String?
)