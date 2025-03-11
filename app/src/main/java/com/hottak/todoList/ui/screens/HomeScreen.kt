package com.hottak.todoList.ui.screens

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hottak.todoList.R
import com.hottak.todoList.ui.components.LargeBlackButton
import com.hottak.todoList.ui.components.LargeMainTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.hottak.todoList.ui.components.GoogleSignInButton
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, googleSignInClient: GoogleSignInClient) {
    val auth = FirebaseAuth.getInstance()
    val isUserLoggedIn = remember { mutableStateOf(false) }




    // 로그인 성공 처리
    fun firebaseAuthWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "로그인 성공, 계정 정보: ${account.displayName}")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Log.d("GoogleSignIn", "signInWithCredential:success")
                        isUserLoggedIn.value = true // 로그인 성공시 상태 변경
                    } else {
                        Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    }
                }
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "Google sign in failed", e)
        }
    }

    // 로그인 버튼 클릭 시 동작
    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            firebaseAuthWithGoogle(task)
        }
    }

    fun signInWithGoogle() {
        Log.d("GoogleSignIn", "로그인 버튼 클릭됨")
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .background(Color(0xFFFAF8FF)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LargeMainTitle(16, stringResource(id = R.string.app_Version), FontWeight.Normal)
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_image2),
                    contentDescription = "Home Image",
                    modifier = Modifier.size(150.dp).padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(100.dp))
                LargeMainTitle(36, stringResource(id = R.string.title_text))
                Spacer(modifier = Modifier.height(20.dp))
                LargeMainTitle(16, stringResource(id = R.string.title_message))

                Spacer(modifier = Modifier.height(36.dp))

                // 로그인 버튼 추가
                GoogleSignInButton(
                    onClick = {
                        signInWithGoogle()
                    },
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 로그인 상태가 true일 경우 버튼 표시
                if (isUserLoggedIn.value) {
                    LargeBlackButton(navController, "LIST", "page1", Modifier.fillMaxWidth().padding(horizontal = 76.dp, vertical = 4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    LargeBlackButton(navController, "GALLERY", "page2", Modifier.fillMaxWidth().padding(horizontal = 76.dp, vertical = 4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    LargeBlackButton(navController, "SETTING", "page3", Modifier.fillMaxWidth().padding(horizontal = 76.dp, vertical = 4.dp))
                }
            }
        }
    )
}
