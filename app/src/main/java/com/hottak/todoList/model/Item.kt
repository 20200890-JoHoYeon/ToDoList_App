package com.hottak.todoList.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    @ColumnInfo(name = "documentId") val documentId: String,  // Firestore의 문서 ID
//    @PrimaryKey(autoGenerate = true) //room DB 에서만 사용
//    val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean = false
)

// Item을 ItemData로 변환하는 확장 함수
fun Item.toItemData(): ItemData {
    return ItemData(
        documentId = documentId,  // id → documentId로 변경
//        id = id, //room DB 에서만 사용
        title = title,
        content = content,
        date = date,
        isCompleted = isCompleted
    )
}

// ItemData를 Item으로 변환하는 확장 함수
fun ItemData.toItem(): Item {
    return Item(
        documentId = documentId,  // id → documentId로 변경
//        id = id, //room DB 에서만 사용
        title = title,
        content = content,
        date = date,
        isCompleted = isCompleted
    )
}