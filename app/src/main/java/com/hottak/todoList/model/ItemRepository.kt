package com.hottak.todoList.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

class ItemRepository(private val itemDao: ItemDao) {
    val allItems: LiveData<List<ItemData>> = itemDao.getAllItems().map { list ->
        list.map { it.toItemData() }
    }

    val allCompletedItems: LiveData<List<ItemData>> = itemDao.getAllCompletedItems().map { list ->
        list.map { it.toItemData() }
    }

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    suspend fun deleteItem(item: Item) {
        itemDao.delete(item)
    }

    suspend fun updateItem(item: Item) {
        itemDao.update(item)
    }
}