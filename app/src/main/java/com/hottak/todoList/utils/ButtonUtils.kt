package com.hottak.todoList.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.hottak.todoList.R
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.toItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 상황별 버튼 색깔을 반환하는 함수
@Composable
fun getButtonColors(isEditing: Boolean): ButtonColors {
    return if (!isEditing) { // 수정 모드 버튼 색깔
        ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.todo_blue),
            contentColor = Color.White
        )
    } else { // 기본 모드 버튼 색깔
        ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.todo_blue),
            contentColor = Color.White
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun handleButtonClick(
    isEditing: MutableState<Boolean>,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    context: Context,
    isTodoExpanded: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
    viewModel: ItemViewModel,
    dateInput: MutableState<String>,
    pickerDateInitialValue: MutableState<String>,
    currentDate: MutableState<LocalDate>,
    user: MutableState<FirebaseUser?>,
    navController: NavController,
    refreshTrigger:MutableState<Int>
) {
    val userId = user.value?.uid ?: ""
    if (user.value?.uid.isNullOrEmpty()) {
        // 🔴 다른 기기에서 로그인한 경우 -> 팝업 띄우고 추가/수정 차단
        Log.d("handleButtonClick", "Device mismatch detected. Showing AlertDialog.")
        //Toast.makeText(context, "다른 기기에서 로그인한 경우입니다.", Toast.LENGTH_SHORT).show()
        AlertDialog.Builder(context)
            .setMessage("다른 기기에서 로그인되었습니다.\n추가/수정은 동일 기기에서만 가능합니다.")
            .setPositiveButton("확인") { _, _ ->
                navController.navigate("home")
            }
            .show()
    } else {
        // ✅ 동일 기기에서만 To-Do 추가/수정 가능
        val newDocRef = Firebase.firestore.collection("items").document()
        val documentId = newDocRef.id // Firestore 문서 ID 생성

        if (isEditing.value && editingItem.value != null) {
            if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
                val item = editingItem.value!!
                item.title = userInput.value
                item.content = textInput.value
                item.date = dateInput.value
                pickerDateInitialValue.value = dateInput.value

                //fetchDataFromFirestore(userId)
                viewModel.updateItem(item.toItem(), userId) // Room DB 업데이트
                viewModel.saveItemToFirestore(item.toItem(), userId) // Firestore 업데이트
                refreshTrigger.value++
                isEditing.value = false
                editingItem.value = null
                textInput.value = ""
                userInput.value = ""

                Toast.makeText(context, "To-Do가 수정되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
                val newItem = ItemData(
                    title = userInput.value,
                    content = textInput.value,
                    date = dateInput.value,
                    isCompleted = false,
                    documentId = documentId
                ).toItem()

                viewModel.insertItem(newItem) // Room DB 저장
                viewModel.saveItemToFirestore(newItem, userId) // Firestore 저장

                pickerDateInitialValue.value = dateInput.value
                currentDate.value = LocalDate.parse(
                    dateInput.value.split(" ")[0],
                    DateTimeFormatter.ofPattern("yy-MM-dd")
                )

                textInput.value = ""
                userInput.value = ""

                Toast.makeText(context, "진행중인 To-Do에 추가되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            if (!isTodoExpanded.value) {
                isTodoExpanded.value = true
            }

        }
    }
}
