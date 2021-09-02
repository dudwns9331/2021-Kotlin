package com.example.summer_part3_chapter06

/**
 * 데이터베이스 쿼리에 대한 상수 값 지정
 */
class DBKey {
    companion object {
        // Articles : 판매 정보에 대한 것
        const val DB_ARTICLES = "Articles"

        // Users : DB 사용자
        const val DB_USERS = "Users"

        // chat : 채팅 하나에 대한 내역
        const val CHILD_CHAT = "chat"

        // Chats : 채팅방에 대한 내역
        const val DB_CHATS = "Chats"
    }
}