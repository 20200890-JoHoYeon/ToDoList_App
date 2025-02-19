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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ItemData @RequiresApi(Build.VERSION_CODES.O) constructor(var title: String, var content: String, var date: String)
@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDate(): String {
    val currentDateTime = LocalDateTime.now() // 현재 날짜와 시간
    val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Page1ListScreen() {
    //입력받을 필드(사용자가 입력한 제목, 사용자가 입력한 내용)
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }
    //추가된 아이템을 저장하는 리스트 (진행중인 Todo 리스트와 완료된 Todo 리스트)
    val items = remember { mutableStateListOf<ItemData>() }
    val completionItems = remember { mutableStateListOf<ItemData>() }
    //안드로이드의 Context 객체
    val context = LocalContext.current
    //상태 변수 선언 (진행중인 Todo 리스트가 확장되어 있는지 여부, 완료된 Todo 리스트가 확장되어 있는지 여부)
    val isTodoExpanded = remember { mutableStateOf(false) }
    val isCompletedTodoExpanded = remember { mutableStateOf(false) }
    //수정 상태 관리 변수(편집 모드인지 여부, 현재 편집 중인 아이템)
    val isEditing = remember { mutableStateOf(false) }
    val editingItem = remember { mutableStateOf<ItemData?>(null) }


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
                isEditing = isEditing,
                editingItem = editingItem

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
                isCompletedTodoExpanded = isCompletedTodoExpanded,  // 상태 전달
                isEditing = isEditing,
                editingItem = editingItem
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
                colors =
                if(!isEditing.value) { // 수정 모드 버튼 색깔
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A4174),
                        contentColor = Color.White
                    )
                } else { // 기본 모드 버튼 색깔
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A4174),
                        contentColor = Color.White
                    )
                },

                onClick = {
                    if (isEditing.value && editingItem.value != null) {
                        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
                            val item = editingItem.value!!
                            item.title = userInput.value
                            item.content = textInput.value
                            item.date = getCurrentDate()
                            isEditing.value = false
                            editingItem.value = null
                            Toast.makeText(context, "ToDo가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        if (userInput.value.isEmpty()) {
                            Toast.makeText(context, "제목을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                        } else if (textInput.value.isEmpty()) {
                            Toast.makeText(context, "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }


                    } else {
                        if (userInput.value.isNotEmpty() && textInput.value.isNotEmpty()) {
                            items.add(ItemData(userInput.value, textInput.value, getCurrentDate()))
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
    isCompletedTodoExpanded: MutableState<Boolean>,  // 상태 값은 MutableState로 받아야 함
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>
) {

    // Function to add item to completionItems and manage the expanded state
    val addItemToCompleted = { item: ItemData ->
        completionItems.add(ItemData(item.title, item.content, getCurrentDate()))
        Toast.makeText(context, "ToDo가 완료되었습니다.", Toast.LENGTH_SHORT).show()

        // Ensure the completed ToDo section is expanded if necessary
        if (!isCompletedTodoExpanded.value) {
            isCompletedTodoExpanded.value = true
        }
    }

    // Function to add item to items (in-progress list) and manage the expanded state
    val addItemToInProgress = { item: ItemData ->
        items.add(ItemData(item.title, item.content, getCurrentDate()))
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
        LaunchedEffect(editingItem.value) {
            userInput.value = editingItem.value?.title ?: ""
            textInput.value = editingItem.value?.content ?: ""
        }
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
            if(!isEditing.value){// 수정이 아닐때만

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
            Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
        }
        fun handleCheckedChange(checked: Boolean, item: ItemData, isInProgress: Boolean) {
            if (isInProgress) {
                if (checked) {
                    addItemToCompleted(item)
                    items.remove(item)
                }
            } else {
                if (!checked) {
                    addItemToInProgress(item)
                    completionItems.remove(item)
                }
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
                            isEditing = isEditing,
                            editingItem = editingItem,
                            isInProgress = true,
                            onDelete = onDeleteItem,
                            onCheckedChange = { checked, item ->
                                handleCheckedChange(checked, item, true)
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
                            isEditing = isEditing,
                            editingItem = editingItem,
                            isInProgress = false,
                            onDelete = onDeleteItem,
                            onCheckedChange = { checked, item ->
                                handleCheckedChange(checked, item, false)
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
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
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
        if (!isEditing.value || editingItem.value != item) {
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
        }

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

        Column(
            modifier = Modifier
                .padding(start = 10.dp),
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
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Button(
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A4174),
                    contentColor = Color.White
                ),
                    onClick = {isEditing.value = true; editingItem.value = item }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp) // 아이콘 크기 고정
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))  // 두 줄 사이에 여백 추가

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        onDelete(item) // Call onDelete when the delete button is clicked
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp) // 아이콘 크기 고정
                    )
                }
            }
        }
    }
}


