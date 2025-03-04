package com.hottak.todoList.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hottak.todoList.ui.components.TopBar

@Composable//@Composable이라는 주석이 달린 일반 함수입니다. 이 함수는 UI 요소를 반환
fun Page3SettingScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = {

        },
        content = { innerPadding ->
            SettingContent(
                innerPadding = innerPadding,
            )
        }
    )
}

@Composable
fun SettingContent(innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        items((0..9).toList()) { index ->
            SettingItem(index)
        }
    }
}

@Composable
fun SettingItem(index: Int) {
    var isChecked by remember { mutableStateOf(false) } // 초기값 false
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 제목
            Text(
                text = "설정 항목 $index",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // 설명 텍스트
            Text(
                text = "설정 항목에 대한 설명 $index",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // 스위치 (예시: 토글 스위치)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "스위치 항목", fontSize = 14.sp)

                Switch(checked = isChecked, onCheckedChange = {
                    isChecked = it
                })
            }

            // 버튼 (예시: 버튼)
            Button(
                onClick = { /* 버튼 클릭 시 동작 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("설정 적용")
            }
        }
    }
}