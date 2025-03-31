package com.hottak.todoList

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.hottak.todoList.ui.theme.MyApplicationTheme
import com.hottak.todoList.ui.screens.HomeScreen
import com.hottak.todoList.ui.screens.Page1ListScreen
import com.hottak.todoList.ui.screens.Page2GalleryScreen
import com.hottak.todoList.ui.screens.Page3SettingScreen
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this) // Firebase가 이미 초기화되지 않았다면 이 줄이 중요합니다.

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
                val user = remember { mutableStateOf(auth.currentUser) } // 로그인 상태 저장


                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController,
                        googleSignInClient = googleSignInClient, user) }
                    composable("page1/{date}") { backStackEntry ->
                        val page2MoveItemDate = backStackEntry.arguments?.getString("date") ?: "defaultDate"
                        Page1ListScreen(navController, page2MoveItemDate, user)
                    }
                    composable("page2") { Page2GalleryScreen(navController) }
                    composable("page3") { Page3SettingScreen(
                        navController,
                        googleSignInClient = googleSignInClient, user
                    ) }
                }
            }
        }
    }
}
