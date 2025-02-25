package com.hottak.todoList.utils

// ButtonUtils.kt
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
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.toItem

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
    context: Context,
    isTodoExpanded: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
    viewModel: ItemViewModel
) {
    if (isEditing.value && editingItem.value != null) {
        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {

            val item = editingItem.value!!
            item.title = userInput.value
            item.content = textInput.value
            item.date = getCurrentDate()
            viewModel.updateItem(item.toItem())
            isEditing.value = false
            editingItem.value = null
            Log.d("test", "Updated items: $item")
            Toast.makeText(context, "ToDo가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
        if (userInput.value.isEmpty()) {
            Toast.makeText(context, "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        } else if (textInput.value.isEmpty()) {
            Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    } else {
        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
            viewModel.insertItem(ItemData(title = userInput.value, content = textInput.value, date = getCurrentDate(), isCompleted = false).toItem())
            Log.d("test", "insert items")
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