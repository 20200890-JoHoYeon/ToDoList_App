package com.hottak.todoList.ui.screens

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hottak.todoList.R
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.ItemViewModelFactory
import com.hottak.todoList.ui.components.ConfirmationDialog

import com.hottak.todoList.ui.components.TopBar
import com.hottak.todoList.utils.handleLoginRestriction

@Composable
fun Page3SettingScreen(
    navController: NavHostController,
    googleSignInClient: GoogleSignInClient,
    user: MutableState<FirebaseUser?>
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    // 기본 설정
    val appContext = context.applicationContext as Application
    val viewModelFactory = ItemViewModelFactory(appContext)
    val viewModel: ItemViewModel = viewModel(factory = viewModelFactory)

    Scaffold(
        containerColor = Color.White,
        topBar = { TopBar() },
        content = { innerPadding ->
            SettingContent(
                innerPadding = innerPadding,
                navController = navController,
                auth = auth,
                googleSignInClient = googleSignInClient,
                context = context,
                viewModel = viewModel,
                user = user
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
    context: Context,
    viewModel: ItemViewModel,
    user: MutableState<FirebaseUser?>
) {
    var showDeleteDataDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp),

    ) {
        Text(
            text = "설정",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 1️⃣ 로그아웃 (가장 많이 쓰는 기능이므로 상단 배치)
        SettingCard(title = "로그아웃", description = "현재 계정에서 로그아웃합니다.", buttonText = "로그아웃") {
            handleLoginRestriction(
                context = context,
                navController = navController,
                alertMessage = "다른 기기에서 로그인되었습니다.\n현재 기기에서 로그아웃되었습니다.",
                onSuccess = { signOutFromGoogle(auth, googleSignInClient, navController, context) },  // 이 부분이 중요!
                user = user
            )
        }

        // 2️⃣ 데이터 삭제 (중간 중요도)
        SettingCard(title = "데이터 삭제", description = "앱의 모든 데이터를 삭제합니다.", buttonText = "삭제하기") {
            handleLoginRestriction(
                context = context,
                navController = navController,
                alertMessage = "다른 기기에서 로그인되었습니다.\n데이터 삭제는 동일 기기에서만 가능합니다.",
                onSuccess = { showDeleteAccountDialog = true },  // 이 부분이 중요!
                user = user
            )


        }

        // 3️⃣ 계정 삭제 (가장 중요하며 신중해야 하는 기능 → 하단 배치)
        SettingCard(
            title = "계정 삭제",
            description = "회원 정보를 삭제하고 탈퇴합니다.",
            buttonColor = colorResource(R.color.todo_blue),  // 기존보다 은은한 빨간색
            buttonText = "탈퇴하기"
        ) {
            handleLoginRestriction(
                context = context,
                navController = navController,
                alertMessage = "다른 기기에서 로그인되었습니다.\n계정 탈퇴는 동일 기기에서만 가능합니다.",
                onSuccess = { showDeleteAccountDialog = true },  // 이 부분이 중요!
                user = user
            )
        }
    }


    // 데이터 삭제 경고 팝업
    if (showDeleteDataDialog) {
        ConfirmationDialog(
            title = "데이터 삭제",
            message = "정말로 모든 데이터를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.",
            onConfirm = {
                deleteUserData(viewModel, user)
                showDeleteDataDialog = false
            },
            onDismiss = { showDeleteDataDialog = false }
        )
    }

    // 계정 삭제 경고 팝업
    if (showDeleteAccountDialog) {
        ConfirmationDialog(
            title = "계정 삭제",
            message = "정말로 계정을 삭제하시겠습니까?\n계정이 삭제되면 복구할 수 없습니다.",
            onConfirm = {
                deleteUserAccount(auth, googleSignInClient, viewModel, navController, context, user)
                showDeleteAccountDialog = false
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }
}

@Composable
fun SettingCard(title: String, description: String, buttonColor: Color = Color.Black, buttonText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = description, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

fun signOutFromGoogle(
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    navController: NavController,
    context: Context
) {
    val userName = auth.currentUser?.displayName ?: "사용자"
    auth.signOut()
    googleSignInClient.signOut().addOnCompleteListener {
        Toast.makeText(context, "$userName 님 로그아웃 완료", Toast.LENGTH_SHORT).show()
        navController.navigate("home") {
            popUpTo("home") { inclusive = true }
        }
    }
}

fun deleteUserData(
    viewModel: ItemViewModel,
    user: MutableState<FirebaseUser?>
) {
    // 🔹 RoomDB와 Firestore에서 모든 아이템 삭제 (viewModel 활용)
    viewModel.deleteItems()
    user.value?.uid?.let { userId ->
        viewModel.deleteAllItemsFromFirestore(userId) // Firestore에서도 삭제
    }

}

fun deleteUserAccount(
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    viewModel: ItemViewModel,
    navController: NavController,
    context: Context,
    user: MutableState<FirebaseUser?>
) {
    // 🔹 Firestore에서 모든 아이템 삭제 (viewModel 활용)
    deleteUserData(viewModel,user)
    // 🔹 Firestore에서 사용자 계정 삭제 (viewModel 활용)
    user.value?.uid?.let { userId ->
        viewModel.deleteUserAccount(userId) // Firestore에서도 삭제
    }
    signOutFromGoogle(auth, googleSignInClient, navController, context)
}


