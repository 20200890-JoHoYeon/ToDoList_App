package com.hottak.todoList.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
// 현재 날짜와 시간 반환 함수
fun getCurrentDate(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}

// 오늘 년도 반환 함수 (형식: yy)
@RequiresApi(Build.VERSION_CODES.O)
fun getTodayYear(): String {
    val currentDate = LocalDate.now()
    return currentDate.year.toString()
}

// 오늘 월 반환 함수 (형식: MM)
@RequiresApi(Build.VERSION_CODES.O)
fun getTodayMonth(): String {
    val currentDate = LocalDate.now()
    return currentDate.monthValue.toString().padStart(2, '0') // 월이 1자리일 경우 0을 추가
}

// 오늘 일 반환 함수 (형식: dd)
@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDay(): String {
    val currentDate = LocalDate.now()
    return currentDate.dayOfMonth.toString().padStart(2, '0') // 월이 1자리일 경우 0을 추가
}

