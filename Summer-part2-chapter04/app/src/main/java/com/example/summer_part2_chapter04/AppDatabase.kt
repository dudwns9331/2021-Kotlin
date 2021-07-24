package com.example.summer_part2_chapter04

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.summer_part2_chapter04.dao.HistoryDao
import com.example.summer_part2_chapter04.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}