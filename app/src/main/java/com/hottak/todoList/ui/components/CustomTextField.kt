package com.hottak.todoList.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: MutableState<String>,
    placeholder: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int = 100,
    modifier: Modifier,
    endPadding: Dp = 20.dp // 기본값 20.dp
) {
    TextField(
        value = value.value,
        onValueChange = {
            if (it.length <= maxLength) {  // 텍스트 길이가 최대 길이보다 작으면 업데이트
                onValueChange(it)
            }
        },
        label = { Text(label) },
        singleLine = false, // 여러 줄로 입력 허용
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White
        ),
        modifier = modifier.padding(start = 20.dp, top = 16.dp, end = endPadding)
    )
}
