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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.hottak.todoList.ui.theme.MyApplicationTheme
import com.hottak.todoList.ui.screens.HomeScreen
import com.hottak.todoList.ui.screens.Page1ListScreen
import com.hottak.todoList.ui.screens.Page2GalleryScreen
import com.hottak.todoList.ui.screens.Page3SettingScreen
import com.hottak.todoList.ui.screens.Page4ReadFileScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ FirebaseAuth 인스턴스 생성
        val auth = FirebaseAuth.getInstance()

        // ✅ GoogleSignInClient 생성
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController,
                        googleSignInClient = googleSignInClient) }
                    composable("page1/{date}") { backStackEntry ->
                        val page2MoveItemDate = backStackEntry.arguments?.getString("date") ?: "defaultDate"
                        Page1ListScreen(navController, page2MoveItemDate)
                    }
                    composable("page2") { Page2GalleryScreen(navController) }
                    composable("page3") { Page3SettingScreen(
                        navController,
                        googleSignInClient = googleSignInClient
                    ) }
                    composable("page4") { Page4ReadFileScreen(navController) }
                }
            }
        }
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
        HomeScreen(navController, googleSignInClient = TODO()) // NavController 전달
        Page1ListScreen(navController , page2MoveItemDate = encodedDate) // NavController 전달
        Page2GalleryScreen(navController) // NavController 전달
        Page3SettingScreen(
            navController,
            googleSignInClient = TODO()
        ) // NavController 전달
        Page4ReadFileScreen(navController) // NavController 전달
    }
}