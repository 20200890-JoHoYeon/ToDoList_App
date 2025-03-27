package com.hottak.todoList.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyConsentDialog(
    onConsentGiven: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var emailChecked by remember { mutableStateOf(false) }
    var deviceIdChecked by remember { mutableStateOf(false) }
    var inputDataChecked by remember { mutableStateOf(false) }
    var selectAllChecked by remember { mutableStateOf(false) }

    selectAllChecked = emailChecked && deviceIdChecked && inputDataChecked

    AlertDialog(
        onDismissRequest = {}, // 뒤로가기 막기
        title = { Text("개인정보 수집 동의", fontSize = 20.sp) },
        text = {
            Column(modifier = Modifier.padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = emailChecked,
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = Color.Gray
                        ), onCheckedChange = { emailChecked = it })
                    Text("이메일 주소 (필수)\n- 로그인 및 계정 식별", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = deviceIdChecked,
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = Color.Gray
                        ), onCheckedChange = { deviceIdChecked = it } )
                    Text("디바이스 ID (필수)\n- 한 계정당 한 기기 사용 제한", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = inputDataChecked,
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = Color.Gray
                        ), onCheckedChange = { inputDataChecked = it } )
                    //Text("입력 데이터 (선택)\n- 사용자 맞춤 서비스 제공", fontSize = 14.sp)
                    Text("입력 데이터 (필수)\n- 사용자 맞춤 서비스 제공", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                // ✅ "모두 선택" 체크박스
                Row(
                    verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectAllChecked,
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = Color.Gray
                        ), onCheckedChange = {
                            val newValue = !selectAllChecked
                            emailChecked = newValue
                            deviceIdChecked = newValue
                            inputDataChecked = newValue
                        }
                    )
                    Text("모두 선택", fontSize = 15.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (emailChecked && deviceIdChecked && inputDataChecked) {
                        onConsentGiven()
                    } else {
                        Toast.makeText(context, "모든 필수 항목에 동의해야 합니다.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = emailChecked && deviceIdChecked && inputDataChecked
            ) {
                Text("동의함")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("동의 안 함")
            }
        }
    )
}
