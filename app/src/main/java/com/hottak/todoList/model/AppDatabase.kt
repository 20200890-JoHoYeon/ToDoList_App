package com.hottak.todoList.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Item::class], version = 2) // 버전 변경 확인!
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 🚀 마이그레이션 코드 (버전 1 → 2)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 예: 새로운 컬럼 추가
                database.execSQL("ALTER TABLE items ADD COLUMN new_column TEXT DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "your-database-name"
                )
                    .addMigrations(MIGRATION_1_2) // 🚀 마이그레이션 추가!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
