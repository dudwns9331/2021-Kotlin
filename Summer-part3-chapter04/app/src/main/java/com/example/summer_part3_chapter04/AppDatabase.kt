package com.example.summer_part3_chapter04

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.summer_part3_chapter04.dao.HistoryDao
import com.example.summer_part3_chapter04.dao.ReviewDao
import com.example.summer_part3_chapter04.model.History
import com.example.summer_part3_chapter04.model.Review

/**
 * 데이터베이스 정의
 * entities History, Review : Dao
 */
@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

/**
 * 데이터베이스를 가져오는 메소드
 * 테스트 작업을 하면서 Database 의 버전이 다르기 떄문에 오류가 난다.
 * 다음 오류를 잡기 위해서 Migration 코드를 짠다.
 */
fun getAppDatabase(context: Context): AppDatabase {

    val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT, PRIMARY KEY(`id`))")
        }
    }
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}