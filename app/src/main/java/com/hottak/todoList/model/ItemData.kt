package com.hottak.todoList.model

import com.google.firebase.firestore.PropertyName

data class ItemData(
    var documentId: String,
//    val id: Long = 0, //room DB 에서만 사용
    var title: String,
    var content: String,
    var date: String,
    var isCompleted: Boolean = false
){
    // 기본 생성자에서 documentId를 빈 문자열로 초기화
    constructor() : this("", "", "", "", false)
}