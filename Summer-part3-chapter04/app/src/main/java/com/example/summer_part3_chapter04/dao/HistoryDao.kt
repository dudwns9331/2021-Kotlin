package com.example.summer_part3_chapter04.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.summer_part3_chapter04.model.History

/**
 * 로컬 데이터베이스를 사용하기위해 함수를 인터페이스로 정의한다.
 * Dao : Database Access Object : DB를 사용해 데이터를 조회하거나 조작하는 기능을 전담하도록 만든 오브젝트
 */
@Dao
interface HistoryDao {

    // 기록 가져오기
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    // 기록 넣기
    @Insert
    fun insertHistory(history: History)

    // 기록 지우기
    @Query("DELETE FROM history WHERE keyword == :keyword")
    fun delete(keyword: String)

}