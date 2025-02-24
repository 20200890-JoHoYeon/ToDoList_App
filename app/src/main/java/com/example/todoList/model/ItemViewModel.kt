package com.example.todoList.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    private val allItems: LiveData<List<Item>>
    private val allCompletedItems: LiveData<List<CompletedItem>>

    init {
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        val completedItemDao = AppDatabase.getDatabase(application).completedItemDao()
        repository = ItemRepository(itemDao, completedItemDao)
        allItems = repository.allItems
        allCompletedItems = repository.allCompletedItems
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun insertCompletedItem(completedItem: CompletedItem) {
        viewModelScope.launch {
            repository.insertCompletedItem(completedItem)
        }
    }

    fun deleteCompletedItem(completedItem: CompletedItem) {
        viewModelScope.launch {
            repository.deleteCompletedItem(completedItem)
        }
    }

    fun updateCompletedItem(completedItem: CompletedItem) {
        viewModelScope.launch {
            repository.updateCompletedItem(completedItem)
        }
    }
}