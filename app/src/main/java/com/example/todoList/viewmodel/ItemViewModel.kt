package com.example.todoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoList.model.ItemRepository
import com.example.todoList.model.ItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val _allItems = MutableStateFlow<List<ItemData>>(emptyList())
    val allItems: StateFlow<List<ItemData>> = _allItems

    init {
        viewModelScope.launch {
            itemRepository.allItems.collect {
                _allItems.value = it
            }
        }
    }

    fun getItemById(itemId: Int): Flow<ItemData?> = itemRepository.getItemById(itemId)

    fun addItem(item: ItemData) {
        viewModelScope.launch {
            itemRepository.insert(item)
        }
    }

    fun deleteItem(item: ItemData) {
        viewModelScope.launch {
            itemRepository.delete(item)
        }
    }

    fun updateItem(item: ItemData) {
        viewModelScope.launch {
            itemRepository.update(item)
        }
    }
}
