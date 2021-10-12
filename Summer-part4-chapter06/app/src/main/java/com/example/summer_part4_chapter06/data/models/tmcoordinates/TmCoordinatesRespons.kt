package com.example.summer_part4_chapter06.data.models.tmcoordinates


import com.google.gson.annotations.SerializedName

data class TmCoordinatesRespons(
    @SerializedName("documents")
    val documents: List<Document>?,
    @SerializedName("meta")
    val meta: Meta?
)