package com.example.summer_part4_chapter01.service

import com.example.summer_part4_chapter01.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/d0d25b18-d04b-439d-a0b0-d1a630db32f3")
    fun listVideos(): Call<VideoDto>
}