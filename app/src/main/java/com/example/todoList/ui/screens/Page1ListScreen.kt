package com.example.todoList.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoList.model.ItemData
import com.example.todoList.model.ItemViewModel
import com.example.todoList.model.ItemViewModelFactory
import com.example.todoList.model.toItem
import com.example.todoList.ui.components.BottomBar
import com.example.todoList.ui.components.CustomTextField
import com.example.todoList.ui.components.TopBar

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Page1ListScreen() {
    // 기본 설정
    val context = LocalContext.current
    val appContext = context.applicationContext as Application
    val viewModelFactory = ItemViewModelFactory(appContext)
    val viewModel: ItemViewModel = viewModel(factory = viewModelFactory)

    // 입력 필드
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }

    // 데이터베이스에서 가져온 모든 아이템 (ItemData로 변환)
    val allItems: List<ItemData> by viewModel.allItems.observeAsState(emptyList())
    val allCompletedItems: List<ItemData> by viewModel.allCompletedItems.observeAsState(emptyList())
    // 진행 중인 항목
    val items = allItems.toMutableStateList()
    // 완료된 항목
    val completionItems = allCompletedItems.toMutableStateList()
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
                items = items,
                completionItems = completionItems,
                context = context,
                isTodoExpanded = isTodoExpanded,
                isCompletedTodoExpanded = isCompletedTodoExpanded,
                isEditing = isEditing,
                editingItem = editingItem
            )
        }
    )
}

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
    editingItem: MutableState<ItemData?>
) {
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
        CustomTextField(
            value = userInput,
            placeholder = "제목을 입력해주세요 (최대 10자)",
            onValueChange = { userInput.value = it },
            label = if (isEditing.value) "Edit Title" else "Enter Title",
            modifier = Modifier.fillMaxWidth(),
            maxLength = 10
        )
        CustomTextField(
            value = textInput,
            placeholder = "내용을 입력해주세죠 (최대 100자)",
            onValueChange = { textInput.value = it },
            label = if (isEditing.value) "Edit Item" else "Enter Item",
            modifier = Modifier.fillMaxWidth(),
        )

        val onDeleteItem: (ItemData) -> Unit = { itemToDelete ->
            if (!isEditing.value) {
                val item = itemToDelete.toItem()
                viewModel.deleteItem(item)
                 if (items.contains(itemToDelete)) {
                    Toast.makeText(context, "진행중인 ToDO 아이템이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                 }else if (completionItems.contains(itemToDelete)) {
                    Toast.makeText(context, "완료된 ToDo 아이템이 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                 }else{
                    Toast.makeText(context, "아이템을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }


            }
            Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
        }
        fun handleCheckedChange(checked: Boolean, item: ItemData, isInProgress: Boolean) {
            if (isInProgress) {
                val updatedItem = item.copy(isCompleted = checked).toItem()
                Log.d("ItemUpdate", "Updated items: $updatedItem")
                viewModel.updateItem(updatedItem)
                if(checked) addItemToCompleted(item)
                items.remove(item)
            }else {
                val updatedItem = item.copy(isCompleted = checked).toItem()
                Log.d("ItemUpdate", "Updated items: $updatedItem")
                viewModel.updateItem(updatedItem)
                completionItems.remove(item)
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
                checked = item.isCompleted,
                onCheckedChange = { isChecked ->
                    onCheckedChange(isChecked, item) // Handle checked change
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
                        onDelete(item)
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


