package com.hottak.todoList.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser

//현재 기기가 로그인 상태인지 체크하는 예외처리 함수 (동시성 문제 해결)
fun handleLoginRestriction(
    context: Context,
    navController: NavController,
    alertMessage: String,
    onSuccess: () -> Unit,
    user: MutableState<FirebaseUser?>
) {
    if (user.value?.uid.isNullOrEmpty()) {
        Log.d("handleButtonClick", "Device mismatch detected. Showing AlertDialog.")
        android.app.AlertDialog.Builder(context)
            .setMessage(alertMessage)
            .setPositiveButton("확인") { _, _ ->
                navController.navigate("home")
            }
            .show()
    } else {
        onSuccess()
    }
}
