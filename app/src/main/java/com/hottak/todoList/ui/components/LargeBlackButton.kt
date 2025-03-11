package com.hottak.todoList.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LargeBlackButton(
    navController: NavController,
    buttonText: String,
    destination: String,
    modifier: Modifier = Modifier
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        ),
        onClick = {
            val finalDestination = if (destination.contains("page1")) {
                val todayDateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                val encodedDate = URLEncoder.encode(todayDateTime, StandardCharsets.UTF_8.toString())
                "page1/$encodedDate"
            } else {
                destination
            }
            navController.navigate(finalDestination)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text=buttonText,
            fontWeight = FontWeight.Medium
        )
    }
}