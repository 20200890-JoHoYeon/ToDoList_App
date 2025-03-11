package com.hottak.todoList.ui.screens
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.hottak.todoList.ui.components.TopBar

@Composable
fun Page3SettingScreen(navController: NavHostController, googleSignInClient: GoogleSignInClient) {
    val auth = FirebaseAuth.getInstance() // ✅ FirebaseAuth 인스턴스 생성

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = { },
        content = { innerPadding ->
            SettingContent(
                innerPadding = innerPadding,
                navController = navController,
                auth = auth,
                googleSignInClient = googleSignInClient,
            )
        }
    )
}

@Composable
fun SettingContent(innerPadding: PaddingValues, navController: NavController, auth: FirebaseAuth, googleSignInClient: GoogleSignInClient) {
    fun signOutFromGoogle() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("GoogleSignIn", "로그아웃 완료")
            navController.navigate("home") // 로그아웃 후 홈 화면으로 이동
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            items((0..2).toList()) { index ->
                SettingItem(index)
            }
        }

        // ✅ 로그아웃 버튼을 Column 맨 아래 배치
        Button(
            onClick = { signOutFromGoogle() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 76.dp, vertical = 16.dp)
        ) {
            Text("로그아웃")
        }
    }
}

@Composable
fun SettingItem(index: Int) {
    var isChecked by remember { mutableStateOf(false) }
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
            Text(
                text = "설정 항목 $index",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "설정 항목에 대한 설명 $index",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "스위치 항목", fontSize = 14.sp)

                Switch(checked = isChecked, onCheckedChange = {
                    isChecked = it
                })
            }

            Button(
                onClick = { /* 버튼 클릭 시 동작 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("설정 적용")
            }
        }
    }
}
