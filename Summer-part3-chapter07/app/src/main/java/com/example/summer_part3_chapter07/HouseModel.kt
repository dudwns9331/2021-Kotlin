package com.example.summer_part3_chapter07

/**
 * HouseModel 은 Adapter 에서 데이터를 처리하기 위해
 * 사용하는 숙소에 대한 정보이다.
 */
data class HouseModel(
    val id: Int,            // 숙소에 대한 ID
    val title: String,      // 숙소 홍보 제목
    val price: String,      // 숙소 가격
    val imgUrl: String,     // 숙소 이미지
    val lat: Double,        // 위도
    val lng: Double         // 경도
)
