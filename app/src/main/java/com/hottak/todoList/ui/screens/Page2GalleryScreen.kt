package com.hottak.todoList.ui.screens

import com.hottak.todoList.ui.components.ItemPopup
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.hottak.todoList.model.ItemData
import com.hottak.todoList.model.ItemViewModel
import com.hottak.todoList.model.ItemViewModelFactory
import com.hottak.todoList.ui.components.TopBar
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Page2GalleryScreen(navController: NavController, user: MutableState<FirebaseUser?>) {
    // 기본 설정
    val context = LocalContext.current
    val appContext = context.applicationContext as Application
    val viewModelFactory = ItemViewModelFactory(appContext)
    val viewModel: ItemViewModel = viewModel(factory = viewModelFactory)


    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = {

        },
        content = { innerPadding ->
            GalleryContent(
                viewModel=viewModel,
                innerPadding = innerPadding,
                navController = navController,

            )
        }
    )
}
@Composable
fun GalleryContent(
    innerPadding: PaddingValues,
    viewModel: ItemViewModel = viewModel(),
    navController: NavController
) {
    val all by viewModel.all.observeAsState(emptyList())
    val showPopup = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf<ItemData?>(null) }

    // 📌 년-월 기준으로 그룹화 ("2025-02" 이런 형식)
    val groupedItems = all.groupBy { it.date.substring(0, 7) }.toSortedMap(Comparator.reverseOrder()) // 🔥 키(년-월)를 내림차순 정렬

    Box(
        Modifier.fillMaxWidth().padding(innerPadding),
    ) {
        // 팝업이 활성화된 경우 ItemPopup 표시
        selectedItem.value?.let { item ->
            if (showPopup.value) {
                ItemPopup(
                    item = item,
                    onDismiss = { showPopup.value = false },
                    navController = navController,
                    mode = true
                )
            }
        }
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.Center,

        ) {
            groupedItems.forEach { (yearMonth, items) ->
                // 📌 구분선 추가 (변환된 날짜 사용)
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatYearMonth(yearMonth), // "25년 02월" 형식으로 변환 📅
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }

                // 📌 실제 아이템 추가
                items(items, key = { it.id }) { item ->
                    val statusText = if (item.isCompleted) "완료" else "진행중"
                    val alphaValue = if (item.isCompleted) 0.5f else 1f

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .alpha(alphaValue)
                            .clickable {
                                selectedItem.value = item
                                showPopup.value = true
                            },
                        elevation = if (item.isCompleted) CardDefaults.cardElevation(defaultElevation = 0.dp) else CardDefaults.cardElevation(defaultElevation = 4.dp) ,
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isCompleted) Color.LightGray else Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.title,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = item.date,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )

                            Text(
                                text = statusText,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (item.isCompleted) Color.Gray else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
fun formatYearMonth(yearMonth: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yy년 MM월", Locale.getDefault())

    return try {
        val date = inputFormat.parse(yearMonth)
        outputFormat.format(date ?: "")
    } catch (e: Exception) {
        yearMonth // 파싱 실패 시 원래 문자열 반환
    }
}