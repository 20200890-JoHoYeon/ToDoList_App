package com.hottak.todoList.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = { Text(text = message, fontSize = 14.sp) },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                colors =  ButtonDefaults.buttonColors()
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("취소")
            }
        }
    )
}
