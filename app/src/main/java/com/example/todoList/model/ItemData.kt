package com.example.todoList.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class ItemData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 자동 증가 ID
    var title: String,
    var content: String,
    var date: Long//String
)