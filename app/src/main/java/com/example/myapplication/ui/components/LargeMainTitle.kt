package com.example.myapplication.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun LargeMainTitle(
    fontSize: Int,
    text: String
) {
    Text(
        text = text, // 이제 text 매개변수를 사용합니다.
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium,
        fontSize = fontSize.sp, // 글씨 크기 키우기
        fontWeight = FontWeight.Bold // 글씨 굵게
    )
}
