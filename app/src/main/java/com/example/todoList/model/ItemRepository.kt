package com.example.todoList.model

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    val allItems: Flow<List<ItemData>> = itemDao.getAllItems()

    suspend fun insert(item: ItemData): Long = itemDao.insert(item)
    suspend fun update(item: ItemData): Int = itemDao.update(item)
    suspend fun delete(item: ItemData): Int = itemDao.delete(item)

    fun getItemById(itemId: Int): Flow<ItemData?> = itemDao.getItemById(itemId)
}