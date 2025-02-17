package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R

@Preview//@Preview 주석이 달린 구성 가능한 함수를 사용하여 UI를 미리보기
@Composable
fun HomeScreenPreview() {
    // NavController를 모킹해서 전달
    val navController = rememberNavController() // 여기서는 실제 NavController를 사용하지 않아도 됩니다.

    HomeScreen(navController = navController)
}

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_image2), // 리소스 이미지 추가 필요
                contentDescription = "Home Image",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "TO-DO LIST",
                textAlign = TextAlign.Center,

                style = MaterialTheme.typography.headlineMedium,
                fontSize = 32.sp, // 글씨 크기 키우기
                fontWeight = FontWeight.Bold // 글씨 굵게
            )

            Text(
                text = "소중한 시간을 효율적으로 관리하고 \n" +
                        "프라이빗하게 기록하는 당신만의 공간",
                textAlign = TextAlign.Center,

                style = MaterialTheme.typography.headlineMedium,
                fontSize = 16.sp, // 글씨 크기 키우기
                fontWeight = FontWeight.Bold // 글씨 굵게
            )
            Spacer(modifier = Modifier.height(26.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, // 버튼의 배경 색상
                    contentColor = Color.White,   // 버튼 내부 텍스트 색상
                    disabledContainerColor = Color.Gray, // 비활성 상태의 배경 색상
                    disabledContentColor = Color.LightGray // 비활성 상태의 텍스트 색상
                ),
                onClick = { navController.navigate("page1") }, shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(250.dp),

                ) {
                Text("LIST")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, // 버튼의 배경 색상
                    contentColor = Color.White,   // 버튼 내부 텍스트 색상
                    disabledContainerColor = Color.Gray, // 비활성 상태의 배경 색상
                    disabledContentColor = Color.LightGray // 비활성 상태의 텍스트 색상
                ),
                onClick = { navController.navigate("page2") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(250.dp)
            ) {
                Text("Gallery")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black, // 버튼의 배경 색상
                    contentColor = Color.White,   // 버튼 내부 텍스트 색상
                    disabledContainerColor = Color.Gray, // 비활성 상태의 배경 색상
                    disabledContentColor = Color.LightGray // 비활성 상태의 텍스트 색상
                ),
                onClick = { navController.navigate("page3") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(250.dp)
            ) {
                Text("Hello")
            }
        }
    }
}

