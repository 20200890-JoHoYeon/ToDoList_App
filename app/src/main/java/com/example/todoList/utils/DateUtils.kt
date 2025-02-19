package com.example.todoList.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
// 현재 날짜와 시간 반환 함수
fun getCurrentDate(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}