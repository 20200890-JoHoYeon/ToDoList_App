package com.example.todoList.model

import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao: ItemDao, private val completedItemDao: CompletedItemDao) {
    val allItems: LiveData<List<Item>> = itemDao.getAllItems()
    val allCompletedItems: LiveData<List<CompletedItem>> = completedItemDao.getAllCompletedItems()

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.delete(item)
    }

    suspend fun updateItem(item: Item) {
        itemDao.update(item)
    }

    suspend fun insertCompletedItem(completedItem: CompletedItem) {
        completedItemDao.insert(completedItem)
    }

    suspend fun deleteCompletedItem(completedItem: CompletedItem) {
        completedItemDao.delete(completedItem)
    }

    suspend fun updateCompletedItem(completedItem: CompletedItem) {
        completedItemDao.update(completedItem)
    }
}