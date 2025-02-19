package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LargeBlackButton(
    navController: NavController,
    buttonText: String,
    destination: String,
    modifier: Modifier = Modifier
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black, // 버튼의 배경 색상
            contentColor = Color.White,   // 버튼 내부 텍스트 색상
            disabledContainerColor = Color.Gray, // 비활성 상태의 배경 색상
            disabledContentColor = Color.LightGray // 비활성 상태의 텍스트 색상
        ),
        onClick = { navController.navigate(destination) },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(buttonText)
    }
}
