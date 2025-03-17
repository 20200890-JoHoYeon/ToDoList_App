package com.hottak.todoList.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

class ItemRepository(private val itemDao: ItemDao) {

    val all: LiveData<List<ItemData>> = itemDao.getAll().map { list ->
        list.map { it.toItemData() }
    }

    val allItems: LiveData<List<ItemData>> = itemDao.getAllItems().map { list ->
        list.map { it.toItemData() }
    }

    val allCompletedItems: LiveData<List<ItemData>> = itemDao.getAllCompletedItems().map { list ->
        list.map { it.toItemData() }
    }

    suspend fun insertItem(item: Item) {
        itemDao.insert(item)
    }

    // 여러 아이템 삽입
    suspend fun insertItems(itemsList: List<Item>) {
        itemDao.insertAll(itemsList) // Room DB에서 여러 아이템을 한 번에 삽입
    }


    suspend fun deleteItem(item: Item) {
        itemDao.delete(item)
    }

    suspend fun updateItem(item: Item) {
        itemDao.update(item)
    }

}