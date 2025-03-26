package com.hottak.todoList.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
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
import android.os.Build
import com.hottak.todoList.utils.getDeviceId

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
    // 다른 기기에서 이미 로그인 된 상태인지 확인하여 UI 유지하는 변수 (해당 변수가 없으면 로그인 시도 후 로그인 성공 ui가 일시적으로 나타남)
    val isMultiLogin = remember { mutableStateOf(false) }

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
                            isCompleted = document.getBoolean("isCompleted") ?: false
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



    fun forceLogoutPreviousDevice(userId: String) {
        val userRef = db.collection("users").document(userId)

        userRef.update("forceLogout", true) // 로그아웃 트리거 설정
            .addOnSuccessListener {
                Log.d("GoogleSignIn", "이전 기기에서 강제 로그아웃 요청 성공")
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "강제 로그아웃 요청 실패", e)
            }
    }


    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user.value = firebaseAuth.currentUser
            isUserLoggedIn.value = user.value != null

            if (isUserLoggedIn.value) {
                user.value?.uid?.let { userId ->
                    val userRef = db.collection("users").document(userId)
                    val currentDeviceId = getDeviceId(context) // 현재 기기 ID 가져오기

                    Log.d("HomeScreen", "User logged in, fetching data for userId: $userId")

                    // Firestore에서 저장된 deviceId 가져오기
                    userRef.get()
                        .addOnSuccessListener { document ->
                            val storedDeviceId = document.getString("deviceId") ?: ""

                            if (storedDeviceId.isNotEmpty() && storedDeviceId != currentDeviceId) {
                                // 다른 기기에서 로그인 중인 경우
                                AlertDialog.Builder(context)
                                    .setTitle("기기 변경 감지")
                                    .setMessage("이 계정이 다른 기기에서 사용 중입니다.\n이 기기로 로그인하면 기존 기기는 로그아웃됩니다.\n계속하시겠습니까?")
                                    .setPositiveButton("예") { _, _ ->
                                        userRef.update("deviceId", currentDeviceId)
                                            .addOnSuccessListener {
                                                Log.d("GoogleSignIn", "기기 변경 승인됨, 다른 기기 로그아웃 처리")
                                                forceLogoutPreviousDevice(userId) // 이전 기기 로그아웃 처리
                                                Toast.makeText(context, "다른 기기에서 로그아웃 처리가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                                fetchDataFromFirestore(userId)
                                            }
                                    }
                                    .setNegativeButton("아니오") { _, _ ->
                                        Log.e("GoogleSignIn", "로그인 차단됨")
                                        Toast.makeText(context, "다른 기기에서 접속 중입니다.\n로그아웃 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                                        auth.signOut()
                                        user.value = null
                                        isUserLoggedIn.value = false
                                    }
                                    .show()
                            } else {
                                // 동일 기기이거나 최초 로그인 -> Firestore에 현재 deviceId 저장
                                userRef.update("deviceId", currentDeviceId)
                                fetchDataFromFirestore(userId) // Firestore 데이터 불러오기
                            }
                        }

                    // 강제 로그아웃 감지
                    userRef.addSnapshotListener { documentSnapshot, _ ->
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            val forceLogout = documentSnapshot.getBoolean("forceLogout") ?: false
                            if (forceLogout) {
                                Log.d("GoogleSignIn", "강제 로그아웃 감지됨, 사용자 로그아웃 처리")
                                auth.signOut()
                                user.value = null
                                isUserLoggedIn.value = false
                                isMultiLogin.value = false

                                // 로그아웃 플래그 초기화
                                userRef.update("forceLogout", false)
                            }
                        }
                    }
                }
                isMultiLogin.value = true
            }
        }

        auth.addAuthStateListener(authStateListener)

        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }





    // 로그인 성공 처리 (한 계정, 한 기기 제한 추가)
    fun firebaseAuthWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "로그인 성공, 계정 정보: ${account?.displayName}")

            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.value = auth.currentUser
                        val currentUser = auth.currentUser
                        isUserLoggedIn.value = true

                        currentUser?.uid?.let { userId ->
                            val currentDeviceId = getDeviceId(context)
                            val userRef = db.collection("users").document(userId)

                            // Firestore에서 저장된 deviceId 가져오기
                            userRef.get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        isMultiLogin.value = false

                                        val storedDeviceId = document.getString("deviceId")

                                        if (storedDeviceId == null || storedDeviceId == currentDeviceId) {
                                            // 저장된 deviceId가 없거나 현재 기기와 일치하면 정상 로그인
                                            Log.d("GoogleSignIn", "기기 확인 완료, 로그인 성공")
                                            userRef.update("deviceId", currentDeviceId) // 현재 기기로 갱신
                                            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                                            isMultiLogin.value = true
                                            isUserLoggedIn.value = true
                                            fetchDataFromFirestore(userId)
                                        } else {
//                                            // 다른 기기에서 로그인 중인 경우
//                                            AlertDialog.Builder(context)
//                                                .setTitle("기기 변경 감지")
//                                                .setMessage("다른 기기에서 로그인된 상태입니다.\n현재 기기로 로그인하면 기존 기기는 로그아웃됩니다.\n진행하시겠습니까?")
//                                                .setPositiveButton("예") { _, _ ->
//                                                    userRef.update("deviceId", currentDeviceId)
//                                                        .addOnSuccessListener {
//                                                            Log.d("GoogleSignIn", "기기 변경 승인됨, 다른 기기 로그아웃 처리")
//                                                            forceLogoutPreviousDevice(userId)
//                                                            Toast.makeText(context, "다른 기기에서 로그아웃 처리가 완료되었습니다.", Toast.LENGTH_SHORT).show()
//                                                            fetchDataFromFirestore(userId)
//                                                        }
//
//                                                }
//                                                .setNegativeButton("아니오") { _, _ ->
//                                                    Log.e("GoogleSignIn", "로그인 차단됨")
//                                                    Toast.makeText(context, "다른 기기에서 접속 중입니다.\n로그아웃 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
//                                                    auth.signOut()
//                                                    user.value = null
//                                                    isUserLoggedIn.value = false
//                                                }
//                                                .show()
                                        }
                                    } else {
                                        // 첫 로그인 시 현재 기기 저장
                                        Log.d("GoogleSignIn", "첫 로그인, 기기 등록 완료")
                                        userRef.set(mapOf("deviceId" to currentDeviceId))
                                        isMultiLogin.value = true
                                        Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                                        fetchDataFromFirestore(userId)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("GoogleSignIn", "Firestore 데이터 조회 실패", e)
                                }
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

                if (!isUserLoggedIn.value || !isMultiLogin.value) { // 로그인 상태가 false일 경우 버튼 표시
                    // 로그인 버튼 추가
                    GoogleSignInButton( onClick = { signInWithGoogle() }, modifier = Modifier)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                else if (isUserLoggedIn.value && isMultiLogin.value) { // 로그인 상태가 true일 경우 버튼 표시
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
