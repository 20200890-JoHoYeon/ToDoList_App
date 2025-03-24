package com.hottak.todoList.ui.screens
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hottak.todoList.ui.components.TopBar

@Composable
fun Page3SettingScreen(navController: NavHostController, googleSignInClient: GoogleSignInClient) {
    val auth = FirebaseAuth.getInstance() // ✅ FirebaseAuth 인스턴스 생성
    val context = LocalContext.current
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
                context = context,
            )
        }
    )
}

@Composable
fun SettingContent(
    innerPadding: PaddingValues,
    navController: NavController,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    context: Context
) {
    fun proceedWithSignOut(userName: String) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("GoogleSignIn", "$userName 님 로그아웃 완료")
            Toast.makeText(context, "$userName 님 로그아웃 완료", Toast.LENGTH_SHORT).show()

            // 홈 화면으로 이동하면서 기존 백 스택 제거
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    fun signOutFromGoogle() {
        val userName = auth.currentUser?.displayName ?: "사용자"
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            // Firestore에서 deviceId 초기화 후 로그아웃 실행
            userRef.update("deviceId", null)
                .addOnSuccessListener {
                    Log.d("GoogleSignIn", "Firestore deviceId 초기화 완료")
                    proceedWithSignOut(userName)
                }
                .addOnFailureListener { e ->
                    Log.e("GoogleSignIn", "Firestore deviceId 초기화 실패", e)
                    proceedWithSignOut(userName) // 실패해도 로그아웃 진행
                }
        } else {
            proceedWithSignOut(userName) // userId가 없을 경우 바로 로그아웃
        }
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
// 추후에 추가할 세팅 옵션 ui
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//        ) {
//            items((0..2).toList()) { index ->
//                SettingItem(index)
//            }
//        }

        // ✅ 로그아웃 버튼을 Column 맨 아래 배치
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp),
            onClick = { signOutFromGoogle() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 76.dp, vertical = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text="Logout",
                fontWeight = FontWeight.Medium
            )
        }
    }
}
// 추후에 추가할 세팅 옵션 ui
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
