package com.example.summer_part3_chapter04.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 해당 값들은 Room 라이브러리를 통해서 로컬 데이터베이스에
 * 저장되는 값들로 기본키와 해당 열에 대한 값을 지정해준다.
 */
@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "keyword") val keyword: String?
)
