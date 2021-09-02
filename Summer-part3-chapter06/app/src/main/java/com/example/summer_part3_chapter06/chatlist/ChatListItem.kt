package com.example.summer_part3_chapter06.chatlist

/**
 * 채팅방 목록을 보여주는 곳에 들어가는 ListItem data
 */
data class ChatListItem(
    val buyerId: String,    // 구매자 ID
    val sellerId: String,   // 판매자 ID
    val itemTitle: String,  // 아이템 제목
    val key: Long           // key 값
) {
    // 파이어베이스에서 바로 데이터 클래스를 가져오려면 초기값에 대한 설정이 필요하다.
    constructor() : this("", "", "", 0)
}
