package com.hottak.todoList.ui.components

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import android.util.Log

@Composable
fun PrivacyConsentDialog(
    onConsentGiven: () -> Unit,
    onDismiss: () -> Unit,
    user: MutableState<FirebaseUser?>
) {
    val context = LocalContext.current

    var emailChecked by remember { mutableStateOf(false) }
    var deviceIdChecked by remember { mutableStateOf(false) }
    var inputDataChecked by remember { mutableStateOf(false) }
    var selectAllChecked by remember { mutableStateOf(false) }
    val userId: String? = user.value?.uid

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
                        if (userId != null) {
                            saveConsentToFirestore(userId, emailChecked, deviceIdChecked, inputDataChecked) { success ->
                                if (success) {
                                    onConsentGiven() // 동의가 완료된 후 처리
                                } else {
                                    Toast.makeText(context, "동의 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
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
            Button(onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("동의 안 함")
            }
        }
    )
}
fun saveConsentToFirestore(
    userId: String,
    emailChecked: Boolean,
    deviceIdChecked: Boolean,
    inputDataChecked: Boolean,
    onComplete: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    val consentData = hashMapOf(
        "emailChecked" to emailChecked,
        "deviceIdChecked" to deviceIdChecked,
        "inputDataChecked" to inputDataChecked,
        "consentTimestamp" to Timestamp.now() // Firestore의 Timestamp 객체 사용
    )

    Log.d("Firestore", "Attempting to save consent data for user: $userId")
    Log.d("Firestore", "Consent Data: $consentData")

    userRef.set(consentData)
        .addOnSuccessListener {
            // 성공적인 저장 후 로그
            Log.d("Firestore", "✅ 동의 정보가 Firestore에 성공적으로 저장됨!")
            onComplete(true) // 저장 성공 시 onComplete(true) 호출
        }
        .addOnFailureListener { e ->
            // 실패한 경우 로그
            Log.e("Firestore", "❌ Firestore 저장 실패: ${e.message}", e)
            onComplete(false) // 저장 실패 시 onComplete(false) 호출
        }
        .addOnCompleteListener {
            Log.d("Firestore", "Firestore operation completed")
        }
}