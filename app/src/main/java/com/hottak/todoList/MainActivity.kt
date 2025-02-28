package com.hottak.todoList

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hottak.todoList.ui.theme.MyApplicationTheme
import com.hottak.todoList.ui.screens.HomeScreen
import com.hottak.todoList.ui.screens.Page1ListScreen
import com.hottak.todoList.ui.screens.Page2GalleryScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("page1/{date}") { backStackEntry ->
                        val page2MoveItemDate = backStackEntry.arguments?.getString("date") ?: "defaultDate"
                        Page1ListScreen(navController, page2MoveItemDate)
                    }
                    composable("page2") { Page2GalleryScreen(navController) }
                }
            }
        }
    }

    // 🔹 현재 날짜 및 시간을 포맷팅 후 URL Encoding 적용
    @RequiresApi(Build.VERSION_CODES.O)
    fun getEncodedCurrentDate(): String {
        val todayDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return URLEncoder.encode(todayDateTime, StandardCharsets.UTF_8.toString())
    }
}

//모킹
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController() // NavController 생성
    val todayDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) // 오늘 날짜 및 시간
    val encodedDate = URLEncoder.encode(todayDateTime, StandardCharsets.UTF_8.toString())
    MyApplicationTheme {
        HomeScreen(navController) // NavController 전달
        Page1ListScreen(navController , page2MoveItemDate = encodedDate) // NavController 전달
        Page2GalleryScreen(navController) // NavController 전달
        //Page3HelloScreen(navController) // NavController 전달
    }
}