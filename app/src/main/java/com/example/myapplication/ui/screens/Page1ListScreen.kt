package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import java.time.format.DateTimeFormatter
import kotlin.math.log

data class ItemData @RequiresApi(Build.VERSION_CODES.O) constructor(val author: String, val content: String, val date: String)
@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val currentDateTime = java.time.LocalDateTime.now() // 현재 날짜와 시간
    val formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss")
    return currentDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Page1ListScreen() {
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<ItemData>() }
    val CompletionItems = remember { mutableStateListOf<ItemData>() }
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = { BottomBar(userInput, textInput, items, context) },
        content = { innerPadding ->
            PageContent(innerPadding, userInput, textInput, items, CompletionItems)
        }
    )
}

@Composable
fun TopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_image2),
            contentDescription = "Home Image",
            modifier = Modifier.size(50.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBar(
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>,
    context: Context
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = {
                    if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
                        // 날짜를 현재 날짜로 설정
                        val currentDateTime = getCurrentDate()
                        items.add(ItemData(userInput.value, textInput.value, currentDateTime))
                        textInput.value = ""
                        userInput.value = ""
                    } else if (userInput.value.isEmpty()) {
                        Toast.makeText(context, "작성자를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                    } else if (textInput.value.isEmpty()) {
                        Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "Add Item")
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
                Text(text = "Delete Item")
            }
//            Button(
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Black,
//                    contentColor = Color.White
//                ),
//                onClick = {
//                    context.startActivity(Intent(context, MainActivity::class.java))
//                }
//            ) {
//                Text(text = "Go Home")
//            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageContent(
    innerPadding: PaddingValues,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>,
    CompletionItems: SnapshotStateList<ItemData>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userInput.value,
            onValueChange = { userInput.value = it },
            label = { Text("Enter Author") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 16.dp, end = 20.dp)
        )

        TextField(
            value = textInput.value,
            onValueChange = { textInput.value = it },
            label = { Text("Enter Item") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 16.dp, end = 20.dp)
        )

        var isTodoExpanded by remember { mutableStateOf(true) } // for the "진행중인 ToDo" section
        var isCompletedTodoExpanded by remember { mutableStateOf(false) } // for the "완료된 ToDo" section

        Column(modifier = Modifier.fillMaxSize()) {

            // 진행중인 ToDo title and IconButton in a Row with clickable Text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "진행중인 ToDo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (items.isEmpty()) Color.LightGray else Color.Black, // Change color based on item count
                    modifier = Modifier
                        .weight(1f) // Allow title to take up available space
                        .clickable {
                            isTodoExpanded =
                                !isTodoExpanded // Toggle expand/collapse when the text is clicked
                        }
                )

                // Show item count next to the title
                if (items.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(24.dp)
                            .background(Color.Black, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = items.size.toString(),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                IconButton(onClick = { isTodoExpanded = !isTodoExpanded }) {
                    Icon(
                        imageVector = if (isTodoExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }

            // 진행중인 ToDo items
            if (isTodoExpanded) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(items) { item ->
                        ItemRow(
                            item = item,
                            isInProgress = true,
                            onCheckedChange = { checked, item ->
                                if (checked) {
                                    val completionDateTime = getCurrentDate()
                                    CompletionItems.add(
                                        ItemData(
                                            item.author,
                                            item.content,
                                            completionDateTime
                                        )
                                    )
                                    items.remove(item)
                                }
                            }
                        )
                    }
                }
            }


            // 완료된 ToDo title and IconButton in a Row with clickable Text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "완료된 ToDo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (CompletionItems.isEmpty()) Color.LightGray else Color.Black, // Change color based on item count
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isCompletedTodoExpanded =
                                !isCompletedTodoExpanded
                        }
                )

                if (CompletionItems.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(24.dp)
                            .background(Color.Black, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = CompletionItems.size.toString(),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                IconButton(onClick = { isCompletedTodoExpanded = !isCompletedTodoExpanded }) {
                    Icon(
                        imageVector = if (isCompletedTodoExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }

            // 완료된 ToDo items
            if (isCompletedTodoExpanded) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(CompletionItems) { CompletionItem ->
                        ItemRow(
                            item = CompletionItem,
                            isInProgress = false,  // 완료된 항목
                            onCheckedChange = { checked, item ->
                                if (checked == false) {
                                    // 완료된 항목을 진행 중으로 다시 이동
                                    val currentDateTime = getCurrentDate()
                                    items.add(ItemData(item.author, item.content, currentDateTime)) // 진행 중 항목으로 추가
                                    CompletionItems.remove(item) // 완료된 항목에서 제거
                                }
                            }
                        )
                    }
                }
            }

        }
    }

}
@Composable
fun ItemRow(
    item: ItemData,
    isInProgress: Boolean,
    onCheckedChange: (Boolean, ItemData) -> Unit
) {
    var checked by remember { mutableStateOf(false) }

    // 진행 중이 아닌 경우 배경 색상 설정
    val rowBackgroundColor = if (isInProgress) {
        Color.LightGray.copy(alpha = 0.3f) // 진행 중일 때는 연한 회색 배경
    } else {
        Color.White.copy(alpha = 0.5f) // 완료된 항목은 투명도 적용
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
            .background(rowBackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 20.dp)
            .then(Modifier.alpha(if (isInProgress) 1f else 0.5f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = "작성자: ${item.author}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2A4174)
            )
            Text(
                text = "내용: ${item.content}",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = item.date,
                fontSize = 14.sp,
                color = Color.Black
            )

            // isInProgress가 false일 때 체크박스가 체크된 것처럼 보이게 함
            Checkbox(
                checked = if (!isInProgress) true else checked, // 진행 중이 아니면 체크 표시되도록
                onCheckedChange = { isChecked ->
                    checked = isChecked
                    onCheckedChange(isChecked, item) // 체크 상태 변경 시 호출
                    checked = false
                },
                modifier = Modifier.size(40.dp), // 체크박스 크기 조정
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Blue, // 체크된 상태 색상
                    uncheckedColor = Color.Gray // 체크되지 않은 상태 색상
                )
            )
        }
    }
}
