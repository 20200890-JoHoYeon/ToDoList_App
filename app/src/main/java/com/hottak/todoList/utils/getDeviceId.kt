package com.hottak.todoList.utils

import android.content.Context
import android.provider.Settings

// 현재 기기의 Android ID 가져오는 함수
fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}