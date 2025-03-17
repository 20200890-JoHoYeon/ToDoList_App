package com.hottak.todoList.model

data class FireStoreItemData(
    val title: String,
    val content: String,
    val date: String,
    val isCompleted: Boolean = false
)
