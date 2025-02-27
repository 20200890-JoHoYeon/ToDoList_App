package com.hottak.todoList.ui.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.utils.getButtonColors
import com.hottak.todoList.utils.handleButtonClick

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBar(
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    context: Context,
    isTodoExpanded: MutableState<Boolean>,  // 상태 값은 MutableState로 받아야 함
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
    viewModel: ItemViewModel,
    dateInput: MutableState<String>

) {
    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                colors = getButtonColors(isEditing.value),
                onClick = {
                    handleButtonClick(
                        viewModel= viewModel,
                        isEditing = isEditing,
                        userInput = userInput,
                        textInput = textInput,
                        dateInput = dateInput,
                        context = context,
                        isTodoExpanded = isTodoExpanded,
                        editingItem = editingItem
                    )
                }
            ) {
                Text(text = if (isEditing.value) "Edit Complete" else "Add Item")
            }
            if (isEditing.value) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        isEditing.value = !isEditing.value
                        userInput.value=""
                        textInput.value=""

                    }
                ) {
                    Text(text = "cancel")
                }
            }
        }
    }
}
