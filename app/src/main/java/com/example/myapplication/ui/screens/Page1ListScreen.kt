package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import java.time.format.DateTimeFormatter

data class ItemData @RequiresApi(Build.VERSION_CODES.O) constructor(val title: String, val content: String, val date: String)
@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val currentDateTime = java.time.LocalDateTime.now() // 현재 날짜와 시간
    val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Page1ListScreen() {
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<ItemData>() }
    val completionItems = remember { mutableStateListOf<ItemData>() }
    val context = LocalContext.current

    // 상태 변수 선언 (Page1ListScreen에서 관리)
    val isTodoExpanded = remember { mutableStateOf(false) }
    val isCompletedTodoExpanded = remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = {
            BottomBar(
                userInput = userInput,
                textInput = textInput,
                items = items,
                context = context,
                isTodoExpanded = isTodoExpanded,  // 상태 전달

            )
        },
        content = { innerPadding ->
            PageContent(
                innerPadding = innerPadding,
                userInput = userInput,
                textInput = textInput,
                items = items,
                completionItems = completionItems,
                context = context,
                isTodoExpanded = isTodoExpanded,  // 상태 전달
                isCompletedTodoExpanded = isCompletedTodoExpanded  // 상태 전달
            )
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
    context: Context,
    isTodoExpanded: MutableState<Boolean>,  // 상태 값은 MutableState로 받아야 함

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
                        Toast.makeText(context, "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                    } else if (textInput.value.isEmpty()) {
                        Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(context, "진행중인 ToDo에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    if (!isTodoExpanded.value) {
                        isTodoExpanded.value = true
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
                Text(text = "Go Home")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PageContent(
    innerPadding: PaddingValues,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>,
    completionItems: SnapshotStateList<ItemData>,
    context: Context,
    isTodoExpanded: MutableState<Boolean>,  // 상태 값은 MutableState로 받아야 함
    isCompletedTodoExpanded: MutableState<Boolean>  // 상태 값은 MutableState로 받아야 함
) {

    // Function to add item to completionItems and manage the expanded state
    val addItemToCompleted = { item: ItemData ->
        val completionDateTime = getCurrentDate()
        completionItems.add(ItemData(item.title, item.content, completionDateTime))
        Toast.makeText(context, "ToDo가 완료되었습니다.", Toast.LENGTH_SHORT).show()

        // Ensure the completed ToDo section is expanded if necessary
        if (!isCompletedTodoExpanded.value) {
            isCompletedTodoExpanded.value = true
        }
    }

    // Function to add item to items (in-progress list) and manage the expanded state
    val addItemToInProgress = { item: ItemData ->
        val currentDateTime = getCurrentDate()
        items.add(ItemData(item.title, item.content, currentDateTime))
        Toast.makeText(context, "진행중인 ToDo에 추가되었습니다.", Toast.LENGTH_SHORT).show()

        // Ensure the in-progress ToDo section is expanded if necessary
        if (!isTodoExpanded.value) {
            isTodoExpanded.value = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userInput.value,
            onValueChange = {
                // 텍스트 길이가 최대 길이보다 작으면 업데이트
                if (it.length <= 10) {
                    userInput.value = it
                } else {
                    Toast.makeText(context, "최대 10자까지 입력 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            },
            label = { Text("Enter Title") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 16.dp, end = 20.dp),
        )

        TextField(
            value = textInput.value,
            onValueChange = { textInput.value = it },
            label = { Text("Enter Item") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 16.dp, end = 20.dp),
        )


        // Add the onDeleteItem function to remove an item from completionItems
        val onDeleteItem: (ItemData) -> Unit = { itemToDelete ->
            if (completionItems.contains(itemToDelete)) {
                completionItems.remove(itemToDelete)
                Toast.makeText(context, "완료된 ToDo 아이템이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            } else if (items.contains(itemToDelete)) {
                items.remove(itemToDelete)
                Toast.makeText(context, "진행중인 ToDO 아이템이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "아이템을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "진행중인 ToDo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (items.isEmpty()) Color.LightGray else Color.Black,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f)
                        .clickable {
                            if (items.size > 1 || !isTodoExpanded.value) {
                                isTodoExpanded.value = !isTodoExpanded.value
                            }
                        }
                )

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
                } else {
                    isTodoExpanded.value = false
                }

                IconButton(
                    onClick = { isTodoExpanded.value = !isTodoExpanded.value },
                    modifier = if (items.isEmpty()) Modifier.alpha(0.5f) else Modifier.alpha(1f)
                ) {
                    Icon(
                        imageVector = if (isTodoExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }

            if (isTodoExpanded.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(items) { item ->
                        ItemRow(
                            item = item,
                            isInProgress = true,
                            onDelete = onDeleteItem,
                            onCheckedChange = { checked, item ->
                                if (checked) {
                                    addItemToCompleted(item) // Add to completed list
                                    items.remove(item)
                                }
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "완료된 ToDo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (completionItems.isEmpty()) Color.LightGray else Color.Black,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f)
                        .clickable {
                            if (completionItems.size > 1 || !isCompletedTodoExpanded.value) {
                                isCompletedTodoExpanded.value = !isCompletedTodoExpanded.value
                            }
                        }
                )

                if (completionItems.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(24.dp)
                            .background(Color.Black, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = completionItems.size.toString(),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                } else {
                    isCompletedTodoExpanded.value = false
                }

                IconButton(
                    onClick = { isCompletedTodoExpanded.value = !isCompletedTodoExpanded.value },
                    modifier = if (completionItems.isEmpty()) Modifier.alpha(0.5f) else Modifier.alpha(1f)
                ) {
                    Icon(
                        imageVector = if (isCompletedTodoExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }

            if (isCompletedTodoExpanded.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(completionItems) { item ->
                        ItemRow(
                            item = item,
                            isInProgress = false,
                            onDelete = onDeleteItem,
                            onCheckedChange = { checked, item ->
                                if (!checked) {
                                    addItemToInProgress(item) // Add back to in-progress list
                                    completionItems.remove(item)
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
    onCheckedChange: (Boolean, ItemData) -> Unit,
    onDelete: (ItemData) -> Unit // Add this parameter to handle deletion
) {
    var checked by remember { mutableStateOf(false) }

    // 진행 중이 아닌 경우 배경 색상 설정
    val rowBackgroundColor = if (isInProgress) {
        Color.LightGray.copy(alpha = 0.3f) // 진행 중일 때는 연한 회색 배경
    } else {
        Color.White.copy(alpha = 0.5f) // 완료된 항목은 투명도 적용
    }
    val dateParts = item.date.split(" ")

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
        // Checkbox takes 1/6 of the Row width
        Checkbox(
            checked = if (!isInProgress) true else checked,
            onCheckedChange = { isChecked ->
                checked = isChecked
                onCheckedChange(isChecked, item) // Handle checked change
                checked = false
            },
            modifier = Modifier
                .size(50.dp)
                .weight(0.5f),  // 1등분 차지
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Blue,
                uncheckedColor = Color.Gray
            )
        )

        // First Column (Title and Content) takes the remaining space
        Column(
            modifier = Modifier
                .weight(3f)  // 제목과 내용이 차지할 비율
                .padding(start = 16.dp)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2A4174),
                maxLines = 1,  // 한 줄로 제한 (길면 생략부호 처리 가능)
                overflow = TextOverflow.Ellipsis  // 넘치면 ... 처리
            )
            Text(
                text = item.content,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                style = TextStyle(
                    lineBreak = LineBreak.Paragraph,
                )
            )
        }

        // Second Column (Date and Delete Button) takes 2/6 of the Row width
        Column(
            modifier = Modifier
                .weight(2f)  // 2등분 차지
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 년-월-일 표시
            Text(
                text = dateParts.getOrNull(0) ?: "",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))  // 두 줄 사이에 여백 추가

            // 시간:분:초 표시
            Text(
                text = dateParts.getOrNull(1) ?: "",
                fontSize = 14.sp,
                color = Color.Black,
            )
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = {
                    onDelete(item) // Call onDelete when the delete button is clicked
                }
            ) {
                Text(text = "Delete")
            }
        }
    }
}


