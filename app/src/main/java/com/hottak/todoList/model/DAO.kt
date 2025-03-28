package com.hottak.todoList.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)

    // 여러 아이템을 한 번에 삽입하는 메서드
    @Insert(onConflict = OnConflictStrategy.REPLACE) // 중복된 항목은 덮어씁니다.
    suspend fun insertAll(items: List<Item>)

    @Delete
    suspend fun delete(item: Item)

    // 모든 아이템을 한 번에 삭제하는 메서드
    @Query("DELETE FROM items")
    suspend fun deleteAll()

    @Update
    suspend fun update(item: Item)

    @Query("SELECT * FROM items WHERE isCompleted = 0") // 진행 중인 항목만 조회
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isCompleted = 1") // 완료된 항목만 조회
    fun getAllCompletedItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items") // 모든 항목 조회
    fun getAll(): LiveData<List<Item>>

}