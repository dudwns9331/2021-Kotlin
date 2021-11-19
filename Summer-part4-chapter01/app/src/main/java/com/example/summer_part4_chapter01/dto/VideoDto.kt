package com.example.summer_part4_chapter01.dto

import com.example.summer_part4_chapter01.model.VideoModel

data class VideoDto(
    // DTO : Data Transfer Object ➡ VideoModel
    val videos: List<VideoModel>
)
