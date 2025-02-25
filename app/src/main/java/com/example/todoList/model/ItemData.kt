package com.example.todoList.model

data class ItemData(
    val id: Long = 0,
    var title: String,
    var content: String,
    var date: String,
    val isCompleted: Boolean = false
)