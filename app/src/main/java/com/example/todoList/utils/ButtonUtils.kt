package com.example.todoList.utils

// ButtonUtils.kt
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.todoList.model.ItemData

// 상황별 버튼 색깔을 반환하는 함수
@Composable
fun getButtonColors(isEditing: Boolean): ButtonColors {
    return if (!isEditing) { // 수정 모드 버튼 색깔
        ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A4174),
            contentColor = Color.White
        )
    } else { // 기본 모드 버튼 색깔
        ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A4174),
            contentColor = Color.White
        )
    }
}
// 상황별 버튼 클릭 이벤트를 처리하는 함수
@RequiresApi(Build.VERSION_CODES.O)
fun handleButtonClick(
    isEditing: MutableState<Boolean>,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>,
    context: Context,
    isTodoExpanded: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>
) {
    if (isEditing.value && editingItem.value != null) {
        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
            val item = editingItem.value!!
            item.title = userInput.value
            item.content = textInput.value
            item.date = getCurrentDate()
            isEditing.value = false
            editingItem.value = null
            Toast.makeText(context, "ToDo가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
        if (userInput.value.isEmpty()) {
            Toast.makeText(context, "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        } else if (textInput.value.isEmpty()) {
            Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    } else {
        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
            items.add(ItemData(userInput.value, textInput.value, getCurrentDate()))
            textInput.value = ""
            userInput.value = ""
            Toast.makeText(context, "진행중인 ToDo에 추가되었습니다.", Toast.LENGTH_SHORT).show()
        } else if (userInput.value.isEmpty()) {
            Toast.makeText(context, "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        } else if (textInput.value.isEmpty()) {
            Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    if (!isTodoExpanded.value) {
        isTodoExpanded.value = true
    }
}