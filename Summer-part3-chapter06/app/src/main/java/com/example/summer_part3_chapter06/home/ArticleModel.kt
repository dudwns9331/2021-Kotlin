package com.example.summer_part3_chapter06.home

/**
 * Article 이 저장되는 형식
 */
data class ArticleModel(
    val sellerId: String,   // 판매자 ID
    val title: String,      // 제목
    val createdAt: Long,    // 어디에 생성되었는지
    val price: String,      // 가격
    val imageUrl: String,   // 이미지 주소 URL
) {
    // 파이어베이스에서 바로 데이터 클래스를 가져오려면 초기값에 대한 설정이 필요하다.
    constructor() : this("", "", 0, "", "")
}

