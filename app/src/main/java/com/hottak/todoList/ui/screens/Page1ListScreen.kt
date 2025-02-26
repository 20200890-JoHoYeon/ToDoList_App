package com.hottak.todoList.ui.screens

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hottak.todoList.R
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.ItemViewModelFactory
import com.hottak.todoList.model.toItem
import com.hottak.todoList.ui.components.BottomBar
import com.hottak.todoList.ui.components.CustomTextField
import com.hottak.todoList.ui.components.TopBar
import com.hottak.todoList.utils.getTodayMonth
import com.hottak.todoList.utils.getTodayYear
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Page1ListScreen() {
    // 기본 설정
    val context = LocalContext.current
    val appContext = context.applicationContext as Application
    val viewModelFactory = ItemViewModelFactory(appContext)
    val viewModel: ItemViewModel = viewModel(factory = viewModelFactory)

    // 현재 년월 상태
    val currentYearMonth = remember { mutableStateOf(YearMonth.of(getTodayYear().toInt(), getTodayMonth().toInt())) }
    val year = currentYearMonth.value.year
    val month = currentYearMonth.value.monthValue

    // 날짜를 변경하는 함수
    val updateYearMonth: (Int) -> Unit = { offset ->
        currentYearMonth.value = currentYearMonth.value.plusMonths(offset.toLong())
    }

    // 입력 필드
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }

    // 데이터베이스에서 가져온 모든 아이템 (ItemData로 변환)
    val allItems: List<ItemData> by viewModel.allItems.observeAsState(emptyList())
    val allCompletedItems: List<ItemData> by viewModel.allCompletedItems.observeAsState(emptyList())

    // 필터링된 항목을 MutableStateList로 변환
    val items = allItems.toMutableStateList()
    val completionItems = allCompletedItems.toMutableStateList()

    // 필터링된 진행중인 할 일 목록
    val filteredItems = items.filter { item ->
        // Assuming item.date is a String, parse it to LocalDate
        val dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss") // Adjust the pattern if needed
        val itemDate = LocalDateTime.parse(item.date, dateFormatter)

        // Extract the YearMonth from the parsed date
        val itemYearMonth = YearMonth.from(itemDate)

        // Compare the extracted year and month with the current year and month
        itemYearMonth.year == year && itemYearMonth.monthValue == month
    }.toMutableStateList()

    // 필터링된 완료된 할 일 목록
    val filteredCompletionItems = completionItems.filter { item ->
        // Assuming item.date is a String, parse it to LocalDate
        val dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss") // Adjust the pattern if needed
        val itemDate = LocalDateTime.parse(item.date, dateFormatter)

        // Extract the YearMonth from the parsed date
        val itemYearMonth = YearMonth.from(itemDate)

        // Compare the extracted year and month with the current year and month
        itemYearMonth.year == year && itemYearMonth.monthValue == month
    }.toMutableStateList()


    Log.d("test", "진행중인 items 항목:${year},  ${month}")
    Log.d("test", "투두 페이지 진입")
    Log.d("test", "allItems 데이터베이스에서 가져온 아이템 아이템데이터 타입으로 변환: $allItems")
    Log.d("test", "진행중인 items 항목:${items},  ${items.toList()}")
    Log.d("test", "완료된 items 항목:${completionItems},  ${completionItems.toList()}")

    // 상태 변수
    val isTodoExpanded = remember { mutableStateOf(false) }
    val isCompletedTodoExpanded = remember { mutableStateOf(false) }
    val isEditing = remember { mutableStateOf(false) }
    val editingItem = remember { mutableStateOf<ItemData?>(null) }

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = {
            BottomBar(
                viewModel = viewModel,
                userInput = userInput,
                textInput = textInput,
                context = context,
                isTodoExpanded = isTodoExpanded,
                isEditing = isEditing,
                editingItem = editingItem
            )
        },
        content = { innerPadding ->
            PageContent(
                innerPadding = innerPadding,
                viewModel = viewModel,
                userInput = userInput,
                textInput = textInput,
                items = filteredItems,
                completionItems = filteredCompletionItems,
                context = context,
                isTodoExpanded = isTodoExpanded,
                isCompletedTodoExpanded = isCompletedTodoExpanded,
                isEditing = isEditing,
                editingItem = editingItem,
                currentYearMonth = currentYearMonth,
                updateYearMonth = updateYearMonth
            )
        }
    )
}

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PageContent(
    innerPadding: PaddingValues,
    viewModel: ItemViewModel,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>,
    completionItems: SnapshotStateList<ItemData>,
    context: Context,
    isTodoExpanded: MutableState<Boolean>,
    isCompletedTodoExpanded: MutableState<Boolean>,
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
    currentYearMonth: MutableState<YearMonth>,
    updateYearMonth: (Int) -> Unit
) {
    // 삭제 다이얼로그를 위한 변수들
    val showDialog = remember { mutableStateOf(false) }
    val itemToDelete = remember { mutableStateOf<ItemData?>(null) }
    val addItemToCompleted = { itemData: ItemData ->
        val item = itemData.copy(isCompleted = true).toItem()
        viewModel.updateItem(item)
        val updatedItems = viewModel.allItems.value // LiveData의 값을 가져옴
        Log.d("ItemUpdate", "Updated items: $updatedItems")
        Toast.makeText(context, "ToDo가 완료되었습니다.", Toast.LENGTH_SHORT).show()

        if (!isCompletedTodoExpanded.value) {
            isCompletedTodoExpanded.value = true
        }
    }

    fun formatDate(yearMonth: YearMonth): String {
        return yearMonth.format(DateTimeFormatter.ofPattern("yyyy년 MM월"))
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
        // 년월 표시 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { updateYearMonth(-1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 달"
                )
            }

            Text(
                text = formatDate(currentYearMonth.value),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,

            )

            IconButton(onClick = {
                updateYearMonth(1)
                Log.d("test", "날짜 ${currentYearMonth.value}")
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음 달"
                )
            }
        }
        Row() {
            CustomTextField(
                value = userInput,
                placeholder = "제목을 입력해주세요 (최대 12자)",
                onValueChange = { userInput.value = it },
                label = if (isEditing.value) "Edit Title" else "Enter Title",
                modifier = Modifier.fillMaxWidth(),
                maxLength = 12
            )
            IconButton(onClick = {
                updateYearMonth(-1)
                Log.d("test", "날짜 ${currentYearMonth.value}")
            }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "ItemFieldShow"
                )
            }
        }
        CustomTextField(
            value = textInput,
            placeholder = "내용을 입력해주세죠 (최대 100자)",
            onValueChange = { textInput.value = it },
            label = if (isEditing.value) "Edit Item" else "Enter Item",
            modifier = Modifier.fillMaxWidth(),
        )

        val onDeleteItem: (ItemData) -> Unit = { item ->
            if (!isEditing.value) {
                // 어떤 리스트에서 삭제되는지 확인
                val itemType = when {
                    items.contains(item) -> "진행중인"
                    completionItems.contains(item) -> "완료된"
                    else -> null
                }

                if (itemType != null) {
                    itemToDelete.value = item
                    showDialog.value = true
                } else {
                    Toast.makeText(context, "아이템을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        if (showDialog.value && itemToDelete.value != null) {
            val itemType = when {
                items.contains(itemToDelete.value) -> "진행중인"
                completionItems.contains(itemToDelete.value) -> "완료된"
                else -> null
            }

            if (itemType != null) {
                DeleteAlertDialog(
                    showDialog = showDialog,
                    itemType = itemType,
                    itemToDelete = itemToDelete.value!!,
                    viewModel = viewModel,
                    context = context
                )
            } else {
                Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        fun handleCheckedChange(checked: Boolean, item: ItemData, isInProgress: Boolean) {
            if (isInProgress) {
                if (!isEditing.value) {
                    val updatedItem = item.copy(isCompleted = checked).toItem()
                    Log.d("ItemUpdate", "Updated items: $updatedItem")
                    viewModel.updateItem(updatedItem)
                    if (checked) addItemToCompleted(item)
                    items.remove(item)
                }else {
                    Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (!isEditing.value) {
                    val updatedItem = item.copy(isCompleted = checked).toItem()
                    viewModel.updateItem(updatedItem)
                    completionItems.remove(item)
                }else Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
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
fun DeleteAlertDialog(
    showDialog: MutableState<Boolean>,
    itemType: String,
    itemToDelete: ItemData,
    viewModel: ItemViewModel,
    context: Context
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("$itemType ToDo 삭제") },
        text = { Text("정말로 아이템을 삭제하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = {
                viewModel.deleteItem(itemToDelete.toItem())
                showDialog.value = false
                Toast.makeText(
                    context,
                    "$itemType ToDo 아이템이 삭제되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                Text("예")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("아니오")
            }
        }
    )
}



@Composable
fun ItemRow(
    item: ItemData,
    isInProgress: Boolean,
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
    onCheckedChange: (Boolean, ItemData) -> Unit,
    onDelete: (ItemData) -> Unit
) {

    val showPopup = remember { mutableStateOf(false) } // 팝업 상태 관리

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(start = 16.dp, end = 16.dp)
            .then(Modifier.alpha(if (isInProgress) 1f else 0.5f))
            .clickable { showPopup.value = true } // 터치 시 팝업 표시
            .drawBehind {
                val strokeWidth = 1.dp.toPx() // 선 두께
                val y = size.height - strokeWidth / 4 // 하단 위치 조정
                drawLine(
                    color = Color.LightGray, // 밑줄 색상 (변경 가능)
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isEditing.value || editingItem.value != item) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { isChecked ->
                    onCheckedChange(isChecked, item)
                },
                modifier = Modifier.size(50.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Blue,
                    uncheckedColor = Color.Gray
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(3f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorResource(id = R.color.todo_blue), // XML 색상 리소스 사용
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        Row(
            modifier = Modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                IconButton(
                    modifier = Modifier.size(40.dp), // 정사각형 크기 설정
                    onClick = { isEditing.value = true; editingItem.value = item }
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Edit",
                        tint = Color.Black, // 아이콘 색상 블랙
                        modifier = Modifier.size(24.dp)
                    )
                }


                IconButton(
                    modifier = Modifier.size(40.dp), // 정사각형 크기 설정
                    onClick = { onDelete(item) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Delete",
                        tint = Color.Black, // 아이콘 색상 블랙
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }
    }

    if (showPopup.value) {
        AlertDialog(
            onDismissRequest = { showPopup.value = false },
            title = { Text(text = item.title, fontWeight = FontWeight.Bold) },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(min = 100.dp, max = 300.dp) // 최소 높이 & 최대 높이 설정
                        .verticalScroll(rememberScrollState()) // 스크롤 가능하도록 설정
                ) {
                    Column {
                        Text(text = item.content, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "날짜: ${item.date}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPopup.value = false }
                ) {
                    Text("닫기")
                }
            }
        )
    }
}