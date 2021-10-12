package com.example.summer_part4_chapter06.data.models.tmcoordinates


import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("x")
    val x: Double?,
    @SerializedName("y")
    val y: Double?
)