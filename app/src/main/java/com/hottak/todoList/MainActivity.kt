package com.hottak.todoList

import android.content.Context
import android.widget.Toast
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.hottak.todoList.ui.screens.PrivacyConsentDialog


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
                val context = LocalContext.current
                val sharedPreferences = remember { context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE) }
                val isConsentGiven = remember { mutableStateOf(sharedPreferences.getBoolean("PrivacyConsent", false)) }
                var showDialog by remember { mutableStateOf(!isConsentGiven.value) }

                // 팝업 표시 여부에 따라 다르게 처리
                if (showDialog) {
                    PrivacyConsentDialog(
                        onConsentGiven = {
                            sharedPreferences.edit { putBoolean("PrivacyConsent", true) }
                            isConsentGiven.value = true
                            showDialog = false
                            Toast.makeText(context, "개인정보 수집 동의 완료", Toast.LENGTH_SHORT).show()
                        },
                        onDismiss = {
                            Toast.makeText(context, "개인정보 수집 동의 없이는 앱 사용 불가", Toast.LENGTH_SHORT).show()
                            (context as? ComponentActivity)?.finish()
                        }
                    )
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController,
                        googleSignInClient = googleSignInClient, user) }
                    composable("page1/{date}") { backStackEntry ->
                        val page2MoveItemDate = backStackEntry.arguments?.getString("date") ?: "defaultDate"
                        Page1ListScreen(navController, page2MoveItemDate, user)
                    }
                    composable("page2") { Page2GalleryScreen(navController, user) }
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
