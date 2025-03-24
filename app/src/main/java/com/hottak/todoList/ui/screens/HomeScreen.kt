package com.hottak.todoList.ui.screens

import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hottak.todoList.R
import com.hottak.todoList.ui.components.LargeBlackButton
import com.hottak.todoList.ui.components.LargeMainTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.hottak.todoList.model.Item
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.ItemViewModelFactory
import com.hottak.todoList.model.toItem
import com.hottak.todoList.ui.components.GoogleSignInButton
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    googleSignInClient: GoogleSignInClient,
    user: MutableState<FirebaseUser?>
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val appContext = context.applicationContext as Application
    val viewModelFactory = ItemViewModelFactory(appContext)
    val viewModel: ItemViewModel = viewModel(factory = viewModelFactory)

    // 로그인 상태 추적
    val isUserLoggedIn = remember { mutableStateOf(user.value != null) }


    fun fetchDataFromFirestore(userId: String) {
        // Firestore의 users/{userId}/items 경로에서 데이터 가져오기
        val itemsRef = db.collection("users").document(userId).collection("items")

        Log.d("Firestore", "Fetching data for userId: $userId")

        itemsRef.get()
            .addOnSuccessListener { documents ->
                Log.d("Firestore", "Data fetch successful!")
                val itemsList = mutableListOf<Item>()
                for (document in documents) {
                    try {
                        // Firestore에서 ItemData로 변환
                        val firestoreItem = ItemData(
                            documentId = document.getString("documentId") ?: "",
                            title = document.getString("title") ?: "",
                            content = document.getString("content") ?: "",
                            date = document.getString("date") ?: "",
                            isCompleted = document.getBoolean("isCompleted") ?: false // 🔥 null 방지
                        )

                        Log.d("Firestore", "Fetched item: Title = ${firestoreItem.title}, Content = ${firestoreItem.content}, Date = ${firestoreItem.date}, Completed = ${firestoreItem.isCompleted}")

                        // 아이템 추가
                        itemsList.add(firestoreItem.toItem())
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error processing document: ${document.id}", e)
                    }
                }

                // Firestore에서 데이터를 가져온 후 Room DB에 저장
                if (itemsList.isNotEmpty()) {
                    Log.d("Firestore", "Inserting ${itemsList.size} items into Room DB.")
                    viewModel.insertOrUpdateItems(itemsList) // 여러 아이템을 한 번에 저장
                } else {
                    Log.d("Firestore", "No items found in Firestore.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching items: ${e.message}", e)
            }
    }

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        user.value = currentUser
        isUserLoggedIn.value = currentUser != null

        if (isUserLoggedIn.value) {
            currentUser?.uid?.let { userId ->
                Log.d("HomeScreen", "App launched, fetching data for userId: $userId")
                fetchDataFromFirestore(userId)  // 앱 실행 시 Firestore 데이터 가져오기
            }
        }
    }

    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user.value = firebaseAuth.currentUser
            isUserLoggedIn.value = user.value != null

            if (isUserLoggedIn.value) {
                user.value?.uid?.let { userId ->
                    Log.d("HomeScreen", "User logged in, fetching data for userId: $userId")
                    fetchDataFromFirestore(userId)  // 로그인 상태 변경 시 Firestore 데이터 가져오기
                }
            }
        }

        auth.addAuthStateListener(authStateListener)

        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }


    // 로그인 성공 처리
    fun firebaseAuthWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "로그인 성공, 계정 정보: ${account?.displayName}")

            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.value = auth.currentUser
                        Log.d("GoogleSignIn", "user.value ${user.value}")
                        Log.d("GoogleSignIn", "signInWithCredential:success")
                        isUserLoggedIn.value = true // 로그인 성공시 상태 변경

                        // 로그인 후 Firestore에서 데이터 가져오기
                        user.value?.uid?.let { userId ->
                            Log.d("GoogleSignIn", "userId $userId")
                            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                            fetchDataFromFirestore(userId)
                        }
                    } else {
                        Log.e("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    }
                }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google sign in failed", e)
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
                    .padding(bottom = 20.dp),
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

                if (!isUserLoggedIn.value) { // 로그인 상태가 false일 경우 버튼 표시
                    // 로그인 버튼 추가
                    GoogleSignInButton( onClick = { signInWithGoogle() }, modifier = Modifier)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                else if (isUserLoggedIn.value) { // 로그인 상태가 true일 경우 버튼 표시
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
