package com.hottak.todoList.ui.screens

import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
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
import com.hottak.todoList.model.FireStoreItemData
import com.hottak.todoList.model.Item
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.ItemViewModelFactory
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

    // FirebaseAuth의 상태 변화를 감지하여 user 상태 업데이트
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user.value = firebaseAuth.currentUser
            isUserLoggedIn.value = user.value != null
        }
        auth.addAuthStateListener(authStateListener)

        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }


    // Firestore에서 데이터 가져오기
    fun fetchDataFromFirestore(userId: String) {
        val itemsRef = db.collection("users").document(userId).collection("items")

        itemsRef.get()
            .addOnSuccessListener { documents ->
                val itemsList = mutableListOf<Item>()
                for (document in documents) {
                    val firestoreItem = document.toObject(FireStoreItemData::class.java)
                    val item = Item(
                        title = firestoreItem.title,
                        content = firestoreItem.content,
                        date = firestoreItem.date,
                        isCompleted = firestoreItem.isCompleted
                    )
                    itemsList.add(item)

                    // 각 아이템의 로그 출력
                    Log.d("Firestore", "Fetched item: Title = ${item.title}, Content = ${item.content}, Date = ${item.date}, Completed = ${item.isCompleted}")
                }

                // 모든 아이템을 한 번에 Room DB에 저장
                if (itemsList.isNotEmpty()) {
                    viewModel.insertItems(itemsList) // 여러 아이템을 한 번에 저장하는 함수 호출
                } else {
                    Log.d("Firestore", "No items found in Firestore.")
                }
            }
            .addOnFailureListener { e ->
                // 오류 처리: 예를 들어, 사용자에게 오류 메시지 표시
                Log.e("Firestore", "Error fetching items: ${e.message}", e)
            }

    }


    // 로그인 성공 처리
    fun firebaseAuthWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleSignIn", "로그인 성공, 계정 정보: ${account.displayName}")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.value = auth.currentUser
                        Log.d("GoogleSignIn", "user.value ${user.value}")
                        Log.d("GoogleSignIn", "signInWithCredential:success")
                        isUserLoggedIn.value = true // 로그인 성공시 상태 변경

                        // 로그인 후 Firestore에서 데이터 가져오기
                        user.value?.uid?.let { userId ->
                            fetchDataFromFirestore(userId)
                        }

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

//
//    fun saveItemToFirestore(item: Item, userId: String) {
//        if (userId.isBlank()) {
//            Log.e("Firestore", "User ID is empty! Cannot save item.")
//            return
//        }
//
//        val itemRef = db.collection("users").document(userId).collection("items")
//
//        val firestoreItem = FireStoreItemData(item.title, item.content, item.date, item.isCompleted)
//
//        itemRef.document(item.id.toString()) // 고유한 ID 설정 (RoomDB ID 기반)
//            .set(firestoreItem)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Item added successfully with ID: ${item.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.e("Firestore", "Error adding item", e)
//            }
//    }
//
//    fun loadItemsFromFirestore(userId: String, callback: (List<Item>) -> Unit) {
//        if (userId.isBlank()) {
//            Log.e("Firestore", "User ID is empty! Cannot fetch items.")
//            return
//        }
//
//        val itemRef = db.collection("users").document(userId).collection("items")
//
//        itemRef.get().addOnSuccessListener { documents ->
//            val itemList = mutableListOf<Item>()
//            for (document in documents) {
//                val item = document.toObject(FireStoreItemData::class.java)
//                itemList.add(Item(
//                    id = document.id.toLongOrNull() ?: 0L, // ID를 Long으로 변환
//                    title = item.title,
//                    content = item.content,
//                    date = item.date,
//                    isCompleted = item.isCompleted
//                ))
//            }
//            callback(itemList)
//
//            // 로그로 데이터 출력
//            itemList.forEach {
//                Log.d("Firestore", "Item: ${it.title}, ${it.content}, ${it.date}, Completed: ${it.isCompleted}")
//            }
//        }.addOnFailureListener { e ->
//            Log.e("Firestore", "Error fetching items", e)
//        }
//    }


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
