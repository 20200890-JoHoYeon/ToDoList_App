package com.hottak.todoList.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hottak.todoList.R
import com.hottak.todoList.ui.components.LargeBlackButton
import com.hottak.todoList.ui.components.LargeMainTitle

@Preview//@Preview 주석이 달린 구성 가능한 함수를 사용하여 UI를 미리보기
@Composable
fun HomeScreenPreview() {
    // NavController를 모킹해서 전달
    val navController = rememberNavController() // 여기서는 실제 NavController를 사용하지 않아도 됩니다.
    HomeScreen(navController = navController)
}

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { Column(modifier = Modifier
            .fillMaxWidth().padding(bottom = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            LargeMainTitle(16, stringResource(id = R.string.app_Version), FontWeight.Normal)
        }

    },
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_image2),
                    contentDescription = "Home Image",
                    modifier = Modifier.size(150.dp).padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(100.dp))
                LargeMainTitle(36,stringResource(id = R.string.title_text))
                Spacer(modifier = Modifier.height(20.dp))
                LargeMainTitle(16,stringResource(id = R.string.title_message))

                Spacer(modifier = Modifier.height(36.dp))
                LargeBlackButton(navController, "LIST", "page1", Modifier.fillMaxWidth().padding(horizontal = 76.dp))
                Spacer(modifier = Modifier.height(8.dp))
                LargeBlackButton(navController, "GALLERY", "page2", Modifier.fillMaxWidth().padding(horizontal = 76.dp))
//            Spacer(modifier = Modifier.height(8.dp))
//            LargeBlackButton(navController, "Hello", "page3", Modifier.fillMaxWidth().padding(horizontal = 76.dp))
            }
        }
    )
}

