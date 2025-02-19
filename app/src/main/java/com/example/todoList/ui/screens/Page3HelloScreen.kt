package com.example.todoList.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoList.ui.components.TopBar

@Preview//@Preview 주석이 달린 구성 가능한 함수를 사용하여 UI를 미리보기
@Composable//@Composable이라는 주석이 달린 일반 함수입니다. 이 함수는 UI 요소를 반환
fun Page3HelloScreen() {
    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = {

        },
        content = { innerPadding ->
            HelloContent(
                innerPadding = innerPadding,
            )
        }
    )
}

@Composable
fun HelloContent(innerPadding: PaddingValues) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        columns = GridCells.Fixed(2)
    ) {
        items((0..9).toList()) { index ->
            Card(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Hello World! $index",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

            }
        }
    }
}