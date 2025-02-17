package com.example.myapplication.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.myapplication.MainActivity
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun Page1ListScreen() {
    MyApplicationTheme {
        // 상태 변수 초기화
        val items = remember { mutableStateListOf<String>() }
        val userInput = remember { mutableStateOf("") }
        val textInput = remember { mutableStateOf("") }  // 텍스트 입력 상태

        val context = LocalContext.current  // Context를 가져옵니다.

        Column(modifier = Modifier.padding(16.dp)) {
            //작성자 이름 입력 필드
            TextField(
                value = userInput.value,
                onValueChange = { userInput.value = it },
                label = { Text("Enter Author") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done  // 한글 입력 시 문제가 있을 때, 이 설정을 추가해보세요.
                ),
                modifier = Modifier.fillMaxWidth()
            )
            // 텍스트 입력 필드
            TextField(
                value = textInput.value,
                onValueChange = { textInput.value = it },
                label = { Text("Enter Item") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done  // 한글 입력 시 문제가 있을 때, 이 설정을 추가해보세요.
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row로 버튼을 나란히 배치
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Add Item 버튼
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
                            items.add(userInput.value)
                            items.add(textInput.value)
                            textInput.value = ""  // 텍스트 입력 필드 초기화
                        } else if (userInput.value.isEmpty()) {
                            Toast.makeText(context, "작성자를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                        } else if (textInput.value.isEmpty()) {
                            Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(text = "Add Item")
                }

                // 홈으로 가는 버튼 (예시로 MainActivity로 이동)
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        // MainActivity로 이동 (홈으로 가는 버튼)
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                ) {
                    Text(text = "Go Home")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 리스트 표시
            LazyColumn {
                items(items) { item ->
                    Text(text = item, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
