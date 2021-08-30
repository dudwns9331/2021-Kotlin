package com.example.summer_part3_chapter05

/**
 * Firebase database 쿼리에 사용되는 Keyword
 */
class DBKey {
    companion object {
        // 유저 정보
        const val USERS = "Users"

        // 좋아요 정보
        const val LIKED_BY = "likedBy"

        // 종아요
        const val LIKE = "like"

        // 싫어요
        const val DIS_LIKE = "disLike"

        // 유저 아이디
        const val USER_ID = "userId"

        // 유저 이름
        const val NAME = "name"
    }
}