package com.hottak.todoList.model

import android.util.Log
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

    suspend fun insertItems(itemsList: List<Item>) {
        try {
            itemDao.insertAll(itemsList)
            Log.d("RoomDB", "Items inserted successfully")
        } catch (e: Exception) {
            Log.e("RoomDB", "Error inserting items", e)
        }
    }


    suspend fun deleteItem(item: Item) {
        itemDao.delete(item)
    }

    suspend fun updateItem(item: Item) {
        itemDao.update(item)
    }

}