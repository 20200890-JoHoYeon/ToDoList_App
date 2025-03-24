package com.hottak.todoList.ui.screens

import com.hottak.todoList.ui.components.ItemPopup
import android.annotation.SuppressLint
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.hottak.todoList.R
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.ItemViewModelFactory
import com.hottak.todoList.model.toItem
import com.hottak.todoList.ui.components.BottomBar
import com.hottak.todoList.ui.components.CustomTextField
import com.hottak.todoList.ui.components.TopBar
import com.hottak.todoList.utils.getFirstDay
import com.hottak.todoList.utils.getTodayMonth
import com.hottak.todoList.utils.getTodayYear
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Page1ListScreen(
    navController: NavController,
    page2MoveItemDate: String,
    user: MutableState<FirebaseUser?>
) {
    // 기본 설정
    val context = LocalContext.current
    val appContext = context.applicationContext as Application
    val viewModelFactory = ItemViewModelFactory(appContext)
    val viewModel: ItemViewModel = viewModel(factory = viewModelFactory)

    //날짜 포맷
    val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")

    // 입력 필드
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }
    val dateInput = remember { mutableStateOf("") }
    //dateInput 비어있는 경우 오늘 날짜로 초기화
    if (dateInput.value.isEmpty()) {
        val currentDateTime = LocalDateTime.now()
        dateInput.value = currentDateTime.format(formatter)
    }

    Log.d("test", "초기dateInput: ${dateInput.value}")
    val pickerDate = remember { mutableStateOf(LocalDateTime.now()) }
    //월별 필터링 변환 시 데이트 피커의 초기값 매칭용 변수
    //날짜 설정 후 아이템 생성 시 해당 값 데이트 피커 초기값 유지
    val pickerDateInitialValue = remember { mutableStateOf("") }
    // 현재 년월 상태

    val currentDate = remember {
        mutableStateOf(
            try {
                // 1. URL 디코딩  page2MoveItemDate = 25-04-28+06%3A33%3A37 형식
                val decodedDate = URLDecoder.decode(page2MoveItemDate, StandardCharsets.UTF_8.toString())

                // 2. "+"를 공백으로 변환 (시간 부분 처리)
                val formattedDate = decodedDate.replace("+", " ")

                // 3. LocalDateTime으로 변환
                val parsedDateTime = LocalDateTime.parse(formattedDate, formatter)

                // 4. LocalDate만 추출
                parsedDateTime.toLocalDate()
            } catch (e: Exception) {
                // 변환 실패 시 기본 날짜 사용
                LocalDate.of(getTodayYear().toInt(), getTodayMonth().toInt(), getFirstDay().toInt())
            }
        )
    }

    // 상태 변수
    val isTodoExpanded = remember { mutableStateOf(false) }//진행중인 리스트 아코디언
    val isCompletedTodoExpanded = remember { mutableStateOf(false) }//완료된 리스트 아코디언
    val isEditing = remember { mutableStateOf(false) }//수정모드 구분자
    val editingItem = remember { mutableStateOf<ItemData?>(null) }//수정모드 대상 아이템
    val editingItemDate = remember { mutableStateOf<String?>(null) }//수정모드 대상 아이템 날짜
    val isDatePickerVisible =  remember { mutableStateOf(false) }//상단 년월 필터링 설정픽커 구분자


    // 날짜를 변경하는 함수
    val updateYearMonth: (Int) -> Unit = { offset ->
        currentDate.value = currentDate.value.plusMonths(offset.toLong())
        val dateTime = LocalDateTime.of(currentDate.value, LocalTime.now())
        dateInput.value = dateTime.format(formatter)
        if (!isEditing.value) {
            pickerDateInitialValue.value = dateInput.value
        }
    }

    // 데이터베이스에서 가져온 모든 아이템 (ItemData로 변환)
    val allItems: List<ItemData> by viewModel.allItems.observeAsState(emptyList())
    val allCompletedItems: List<ItemData> by viewModel.allCompletedItems.observeAsState(emptyList())

    // 필터링된 항목을 MutableStateList로 변환
    val items = allItems.toMutableStateList()
    val completionItems = allCompletedItems.toMutableStateList()

    // 필터링된 진행중인 할 일 목록
    val filteredItems = items.filter { item ->
        val itemDate = LocalDateTime.parse(item.date, formatter)
        val itemYearMonth = YearMonth.from(itemDate)
        itemYearMonth.year == currentDate.value.year && itemYearMonth.monthValue == currentDate.value.monthValue
    }.toMutableStateList()

    // 필터링된 완료된 할 일 목록
    val filteredCompletionItems = completionItems.filter { item ->
        val itemDate = LocalDateTime.parse(item.date, formatter)
        val itemYearMonth = YearMonth.from(itemDate)
        itemYearMonth.year == currentDate.value.year && itemYearMonth.monthValue == currentDate.value.monthValue
    }.toMutableStateList()

    Log.d("test", "투두 페이지 진입")
    Log.d("test", "allItems 데이터베이스에서 가져온 아이템 아이템데이터 타입으로 변환: $allItems")
    Log.d("test", "진행중인 items 항목:${items},  ${items.toList()}")
    Log.d("test", "완료된 items 항목:${completionItems},  ${completionItems.toList()}")

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
                dateInput = dateInput,
                context = context,
                isTodoExpanded = isTodoExpanded,
                isEditing = isEditing,
                editingItem = editingItem,
                currentDate = currentDate,
                pickerDateInitialValue = pickerDateInitialValue,
                user = user,
            )
        },
        content = { innerPadding ->
            PageContent(
                innerPadding = innerPadding,
                viewModel = viewModel,
                userInput = userInput,
                textInput = textInput,
                dateInput =dateInput,
                items = filteredItems,
                completionItems = filteredCompletionItems,
                context = context,
                isTodoExpanded = isTodoExpanded,
                isCompletedTodoExpanded = isCompletedTodoExpanded,
                isEditing = isEditing,
                editingItem = editingItem,
                editingItemDate = editingItemDate,
                isDatePickerVisible = isDatePickerVisible,
                currentDate = currentDate,
                updateYearMonth = updateYearMonth,
                pickerDate = pickerDate,
                pickerDateInitialValue = pickerDateInitialValue,
                navController = navController,
                user = user

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
    currentDate: MutableState<LocalDate>,
    updateYearMonth: (Int) -> Unit,
    dateInput: MutableState<String>,
    pickerDate: MutableState<LocalDateTime>,
    isDatePickerVisible: MutableState<Boolean>,
    pickerDateInitialValue: MutableState<String>,
    editingItemDate: MutableState<String?>,
    navController: NavController,
    user: MutableState<FirebaseUser?>,

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

    fun formatDate(yearMonth: LocalDate): String {
        return yearMonth.format(DateTimeFormatter.ofPattern("yyyy년 MM월"))
    }

    val openDateTimePickerDialog: () -> Unit = {
        val currentYear = LocalDate.now().year
        val minCalendar = Calendar.getInstance().apply {
            set(currentYear - 50, Calendar.JANUARY, 1) // 최소 연도 설정 (현재 연도 - 50년)
        }
        val maxCalendar = Calendar.getInstance().apply {
            set(currentYear + 10, Calendar.DECEMBER, 31) // 최대 연도 설정 (현재 연도 + 10년)
        }

        val initialDateTime = try {
            if (isEditing.value) {
                editingItemDate.value.let {
                    LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
                }
            } else {
                pickerDateInitialValue.value.let {
                    LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
                } ?: LocalDateTime.now()
            }
        } catch (e: Exception) {
            LocalDateTime.now()
        }

        val year = initialDateTime.year
        val month = initialDateTime.monthValue - 1 // Month is zero-indexed
        val day = initialDateTime.dayOfMonth
        val hour = initialDateTime.hour
        val minute = initialDateTime.minute
        val second = initialDateTime.second

        val datePicker = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val newDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                pickerDate.value = LocalDateTime.of(newDate, pickerDate.value.toLocalTime()) // 날짜 업데이트

                println("선택한 날짜: $selectedYear-${selectedMonth + 1}-$selectedDay")

                // 시간 선택 다이얼로그 표시
                val timePicker = TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        val newDateTime = LocalDateTime.of(
                            selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute, second
                        )
                        Log.d("test", "선택한 날짜 및 시간: $newDateTime")
                        pickerDate.value = newDateTime // 선택한 시간 반영

                        // 날짜 형식 변환하여 dateInput에 저장
                        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
                        dateInput.value = pickerDate.value.format(formatter)
                        Log.d("test", "dateInput: ${dateInput.value}")
                    },
                    hour,
                    minute,
                    false // 24시간 형식 사용 여부 (false = 12시간 형식)
                )
                timePicker.show()
            },
            year,
            month,
            day
        )

        // 최소 및 최대 날짜 설정
        datePicker.datePicker.minDate = minCalendar.timeInMillis
        datePicker.datePicker.maxDate = maxCalendar.timeInMillis

        datePicker.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                text = formatDate(currentDate.value),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    isDatePickerVisible.value = true
                }

            )

            IconButton(onClick = {
                updateYearMonth(1)
                Log.d("test", "날짜 ${currentDate.value}")
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음 달"
                )
            }
        }

        if (isDatePickerVisible.value) {
            YearMonthPickerBottomSheet(
                currentDate = currentDate.value,
                onDateSelected = {
                    selectedDate -> currentDate.value = selectedDate
                    updateYearMonth(0)
                },
                onDismiss = { isDatePickerVisible.value = false }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomTextField(
                value = userInput,
                placeholder = "제목을 입력해주세요 (최대 12자)",
                onValueChange = { userInput.value = it },
                label = if (isEditing.value) "Edit Title" else "Enter Title",
                modifier = Modifier.weight(1f), // TextField가 남는 공간을 차지하도록 설정
                maxLength = 12,
                endPadding= 4.dp,
            )

            IconButton(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp) // 적절한 패딩 추가
                    .size(40.dp), // 버튼 크기 조정 (아이콘이 적절히 들어가도록 설정)

                onClick = {
                    openDateTimePickerDialog()
                    Log.d("test", "날짜dateInput[[[[ ${dateInput.value}")
                    Log.d("test", "날짜pickerDate[[[[ ${pickerDate.value}")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "날짜 선택",
                    tint = Color.Black // 아이콘 색상 변경
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
                // 아이템의 documentId 가져오기(파이어스토어 삭제용도)
                val documentId = itemToDelete.value!!.documentId
                Log.d("test", "삭제할 documentId: $documentId 아이템: ${itemToDelete.value} ")
                DeleteAlertDialog(
                    showDialog = showDialog,
                    itemType = itemType,
                    documentId = documentId,
                    user = user,
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
                    viewModel.updateItem(updatedItem)  // Room DB에서 상태 업데이트
                    // 파이어스토어에 업데이트 반영
                    user.value?.uid?.let { uid ->
                        viewModel.saveItemToFirestore(updatedItem, uid)
                    }
                    if (checked) addItemToCompleted(item)
                    items.remove(item)
                } else {
                    Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (!isEditing.value) {
                    val updatedItem = item.copy(isCompleted = checked).toItem()
                    Log.d("ItemUpdate", "Updated items: $updatedItem")
                    viewModel.updateItem(updatedItem)  // Room DB에서 상태 업데이트
                    // 파이어스토어에 업데이트 반영
                    user.value?.uid?.let { uid ->
                        viewModel.saveItemToFirestore(updatedItem, uid)
                    }
                    completionItems.remove(item)
                } else {
                    Toast.makeText(context, "수정중인 Todo를 완료해주세요.", Toast.LENGTH_SHORT).show()
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
                            isInProgress = true,
                            isEditing = isEditing,
                            editingItem = editingItem,
                            onCheckedChange = { checked, itemData ->
                                handleCheckedChange(checked, itemData, true)
                            },
                            onDelete = onDeleteItem,
                            dateInput = dateInput,
                            editingItemDate = editingItemDate,
                            userInput = userInput,
                            textInput = textInput,
                            navController = navController

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
                            isEditing = isEditing,
                            editingItem = editingItem,
                            onCheckedChange = { checked, itemData ->
                                handleCheckedChange(checked, itemData, false)
                            },
                            onDelete = onDeleteItem,
                            dateInput = dateInput,
                            editingItemDate = editingItemDate,
                            userInput = userInput,
                            textInput = textInput,
                            navController = navController,


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
    context: Context,
    user: MutableState<FirebaseUser?>,
    documentId: String
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("$itemType ToDo 삭제") },
        text = { Text("정말로 아이템을 삭제하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = {
                viewModel.deleteItem(itemToDelete.toItem())
                user.value?.uid?.let { userId ->
                    viewModel.deleteItemFromFirestore(documentId, userId) // Firestore에서도 삭제
                }
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



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemRow(
    item: ItemData,
    isInProgress: Boolean,
    isEditing: MutableState<Boolean>,
    editingItem: MutableState<ItemData?>,
    onCheckedChange: (Boolean, ItemData) -> Unit,
    onDelete: (ItemData) -> Unit,
    dateInput: MutableState<String>,
    editingItemDate: MutableState<String?>,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    navController: NavController
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
                    onClick = {
                        isEditing.value = true
                        editingItem.value = item
                        dateInput.value = item.date
                        editingItemDate.value = item.date
                        userInput.value = editingItem.value?.title ?: ""
                        textInput.value = editingItem.value?.content ?: ""
                        dateInput.value = editingItem.value?.date ?: ""
                    }
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

        ItemPopup(
            item = item,
            onDismiss = { showPopup.value = false },
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun YearMonthPickerBottomSheet(
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = LocalDate.now().year
    val years = (currentYear - 50..currentYear + 10).toList() // 과거 50년 ~ 미래 10년
    val months = (1..12).toList()

    var selectedYear by remember { mutableIntStateOf(currentDate.year) }
    var selectedMonth by remember { mutableIntStateOf(currentDate.monthValue) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "연도 및 월 선택",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // 연도 선택
            ExposedDropdownMenuBox(
                label = "연도",
                options = years,
                selectedOption = selectedYear,
                onOptionSelected = { selectedYear = it }
            )

            // 월 선택
            ExposedDropdownMenuBox(
                label = "월",
                options = months,
                selectedOption = selectedMonth,
                onOptionSelected = { selectedMonth = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                TextButton(
                    modifier = Modifier
                        .width(120.dp)
                        .height(46.dp),
                    onClick = onDismiss
                ) {
                    Text("취소", color = Color.Gray)
                }
                Button(
                    modifier = Modifier
                        .width(120.dp)
                        .height(46.dp),
                    onClick = {
                        onDateSelected(LocalDate.of(selectedYear, selectedMonth, 1))
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.todo_blue))
                ) {
                    Text("확인", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExposedDropdownMenuBox(
    label: String,
    options: List<Int>,
    selectedOption: Int, // 현재 선택된 옵션
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(16.dp),
        ) {
            Text(
                selectedOption.toString(),
                fontSize = 20.sp,
                color = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.reversed().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.toString(),
                            fontSize = 14.sp,
                            color = if (option == selectedOption) colorResource(R.color.todo_date_blue) else Color.Black
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

