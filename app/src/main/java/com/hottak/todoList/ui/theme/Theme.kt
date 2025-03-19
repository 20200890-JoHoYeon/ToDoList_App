package com.hottak.todoList.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,      // 기본(primary) 컬러
    secondary = PrimaryBlue,    // 보조(secondary) 컬러 (버튼 강조)
    background = BackgroundColor, // 배경색
    onPrimary = Color.White,    // primary 컬러 위 텍스트 색상 (흰색)
    onBackground = TextBlack    // 배경색 위 텍스트 색상 (검정)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme, // 항상 흰 테마만 사용
        typography = Typography,
        content = content
    )
}
