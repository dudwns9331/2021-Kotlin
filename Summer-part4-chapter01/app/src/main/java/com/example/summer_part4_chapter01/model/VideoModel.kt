package com.example.summer_part4_chapter01.model

/**
 * VideoModel : mocky에 올라가있는 mock 데이터를 통해서 불러올 정보들
 */
data class VideoModel(
    val title: String,              // 제목
    val sources: String,            // 비디오 소스 링크
    val subtitle: String,           // 부제목
    val thumb: String,              // 썸네일 주소
    val description: String         // 설명
)