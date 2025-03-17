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
import androidx.compose.ui.res.colorResource
import com.google.firebase.auth.FirebaseUser
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
// 상황별 버튼 클릭 이벤트를 처리하는 함수
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
    user: MutableState<FirebaseUser?>
) {
    if (isEditing.value && editingItem.value != null) {
        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {

            val item = editingItem.value!!
            item.title = userInput.value
            item.content = textInput.value
            item.date = getCurrentDate()
            item.date = dateInput.value
            pickerDateInitialValue.value = dateInput.value
            viewModel.updateItem(item.toItem())
            isEditing.value = false
            editingItem.value = null
            Log.d("test", "Updated items: $item")
            textInput.value = ""
            userInput.value = ""
            Toast.makeText(context, "ToDo가 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
        if (userInput.value.isEmpty()) {
            Toast.makeText(context, "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        } else if (textInput.value.isEmpty()) {
            Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    } else {
        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
            viewModel.insertItem(ItemData(title = userInput.value, content = textInput.value, date = dateInput.value, isCompleted = false).toItem())
            user.value?.uid?.let { viewModel.saveItemToFirestore(ItemData(title = userInput.value, content = textInput.value, date = dateInput.value, isCompleted = false).toItem(), it) }
            Log.d("test", "insert items")
            pickerDateInitialValue.value = dateInput.value

            //날짜 설정 후 아이템 생성 시 월별 필터링 해당월로 이동하도록 currentDate 값 매칭 코드
            val shortDate = dateInput.value.split(" ")[0]
            val formatter = DateTimeFormatter.ofPattern("yy-MM-dd")
            currentDate.value = LocalDate.parse(shortDate, formatter)
            Log.d("test", "localDateTime: $shortDate")
            Log.d("test", "current: ${currentDate.value}")

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