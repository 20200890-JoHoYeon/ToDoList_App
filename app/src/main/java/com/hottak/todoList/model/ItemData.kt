package com.hottak.todoList.model

data class ItemData(
    val documentId: String,
//    val id: Long = 0, //room DB 에서만 사용
    var title: String,
    var content: String,
    var date: String,
    val isCompleted: Boolean = false,
){
    // 기본 생성자 추가
    constructor() : this("", "", "","", false)
}