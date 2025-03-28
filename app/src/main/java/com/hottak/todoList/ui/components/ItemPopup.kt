package com.hottak.todoList.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hottak.todoList.model.ItemData
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun ItemPopup(
    item: ItemData,
    onDismiss: () -> Unit, // 팝업 닫기 콜백
    mode: Boolean = false, // 갤러리 페이지에서 실행 시 true → 리스트 페이지 이동 버튼 추가
    navController: NavController // 네비게이션 컨트롤러 추가
) {
    AlertDialog(
        onDismissRequest = onDismiss, // 팝업 외부 클릭 시 닫기
        title = { Text(text = item.title, fontWeight = FontWeight.Bold) },
        text = {
            Box(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(text = item.content, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "날짜: ${item.date}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            Row {
                // 닫기 버튼
                Button(onClick = onDismiss,
                    colors = if (mode) ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    else ButtonDefaults.buttonColors()

                ) {
                    Text("닫기")
                }

                // mode가 true일 때만 '이동' 버튼 표시
                if (mode) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onDismiss() // 팝업 닫기
                            val encodedDate = URLEncoder.encode(item.date, StandardCharsets.UTF_8.toString()) // URL Encoding 적용
                            Log.d("encodedDate", encodedDate)
                            //25-04-28+06%3A33%3A37 형식
                            navController.navigate("page1/$encodedDate") // 안전하게 날짜 전달
                        }
                    ) {
                        Text("이동")
                    }
                }
            }
        }
    )
}
