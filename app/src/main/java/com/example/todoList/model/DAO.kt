package com.example.todoList.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("SELECT * FROM items WHERE isCompleted = 0") // 진행 중인 항목만 조회
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isCompleted = 1") // 완료된 항목만 조회
    fun getAllCompletedItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items") // 모든 항목 조회
    fun getAll(): LiveData<List<Item>>
}