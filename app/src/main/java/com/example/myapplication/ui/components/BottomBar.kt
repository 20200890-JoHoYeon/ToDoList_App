package com.example.myapplication.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.MainActivity
import com.example.myapplication.model.ItemData
import com.example.myapplication.utils.getButtonColors
import com.example.myapplication.utils.handleButtonClick

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBar(
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>,
    context: Context,
    isTodoExpanded: MutableState<Boolean>,  // 상태 값은 MutableState로 받아야 함
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>

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
                        isEditing = isEditing,
                        userInput = userInput,
                        textInput = textInput,
                        items = items,
                        context = context,
                        isTodoExpanded = isTodoExpanded,
                        editingItem = editingItem
                    )
                }
            ) {
                Text(text = if (isEditing.value) "Edit Complete" else "Add Item")
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
            ) {
                Text(text = "Go Home")
            }
        }
    }
}
