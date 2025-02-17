package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class ItemData(val author: String, val content: String)

@Preview
@Composable
fun Page1ListScreen() {
    val userInput = remember { mutableStateOf("") }
    val textInput = remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<ItemData>() }
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        topBar = { TopBar() },
        bottomBar = { BottomBar(userInput, textInput, items, context) },
        content = { innerPadding ->
            PageContent(innerPadding, userInput, textInput, items)
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
                        items.add(ItemData(userInput.value, textInput.value))
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
                Text(text = "Go Home")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageContent(
    innerPadding: PaddingValues,
    userInput: MutableState<String>,
    textInput: MutableState<String>,
    items: SnapshotStateList<ItemData>
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

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                ItemRow(item = item, onCheckedChange = { checked, item ->
                    if (checked) {
                        items.remove(item)  // 항목 삭제
                    }
                })
            }
        }
    }
}

@Composable
fun ItemRow(item: ItemData, onCheckedChange: (Boolean, ItemData) -> Unit) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
            .padding(start= 10.dp, top = 20.dp, bottom = 20.dp, end=10.dp, ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.padding(start = 16.dp)) {
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
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                checked = isChecked
                onCheckedChange(isChecked, item)  // 체크 상태 변경 시 호출
            }
        )
    }
}
