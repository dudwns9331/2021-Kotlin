package com.example.summer_part4_chapter01.service

import com.example.summer_part4_chapter01.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

/**
 * Retrofit 을 이용한 서비스
 */
interface VideoService {
    // Mock 데이터 세부 주소
    @GET("/v3/d0d25b18-d04b-439d-a0b0-d1a630db32f3")
    // VideoDto 를 호출하는 인터페이스
    fun listVideos(): Call<VideoDto>
}