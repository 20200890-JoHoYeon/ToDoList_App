package com.example.myapplication.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview//@Preview 주석이 달린 구성 가능한 함수를 사용하여 UI를 미리보기
@Composable//@Composable이라는 주석이 달린 일반 함수입니다. 이 함수는 UI 요소를 반환
fun Page3HelloScreen() {
    Text(text="Hello World!")
}