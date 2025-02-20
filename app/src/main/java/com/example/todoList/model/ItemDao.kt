package com.example.todoList.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert
    suspend fun insert(item: ItemData): Long // 삽입된 행의 ID 반환

    @Update
    suspend fun update(item: ItemData): Int // 업데이트된 행의 개수 반환

    @Delete
    suspend fun delete(item: ItemData): Int // 삭제된 행의 개수 반환

    @Query("SELECT * FROM item_table ORDER BY date DESC")
    fun getAllItems(): Flow<List<ItemData>>  // Flow로 반환

    @Query("SELECT * FROM item_table WHERE id = :itemId")
    fun getItemById(itemId: Int): Flow<ItemData?>
}
