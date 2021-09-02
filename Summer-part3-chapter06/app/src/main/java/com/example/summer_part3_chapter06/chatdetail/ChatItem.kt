package com.example.summer_part3_chapter06.chatdetail

/**
 * 채팅에 들어가는 Item data
 */
data class ChatItem(
    val senderId: String,   // 보낸사람 ID
    val message: String,    // 메시지
) {
    // 파이어베이스에서 바로 데이터 클래스를 가져오려면 초기값에 대한 설정이 필요하다.
    constructor() : this("", "")
}
