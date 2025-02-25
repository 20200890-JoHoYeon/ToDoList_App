package com.hottak.todoList

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hottak.todoList.ui.theme.MyApplicationTheme
import com.hottak.todoList.ui.screens.HomeScreen
import com.hottak.todoList.ui.screens.Page1ListScreen
import com.hottak.todoList.ui.screens.Page2GalleryScreen
import com.hottak.todoList.ui.screens.Page3HelloScreen

class MainActivity : ComponentActivity() {//ComponentActivity는 Jetpack Compose와 함께 사용할 수 있도록 제공되는 Activity의 확장 클래스
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {//onCreate는 액티비티가 처음 생성될 때 호출되는 메서드 super.onCreate(savedInstanceState)를 호출
        super.onCreate(savedInstanceState)//부모 클래스의 기본 동작을 수행
        enableEdgeToEdge() //안드로이드 시스템 바(edge-to-edge) 적용(화면을 전체화면으로 설정, 네비게이션 바 및 상태 바를 투명하게 만들고 앱 UI가 화면 끝까지 차지)
        setContent { //Compose UI를 렌더링하는 영역
            MyApplicationTheme {//애플리케이션의 테마를 적용하는 래퍼 함수로, 앱의 전체 스타일을 지정
                val navController = rememberNavController() // 네비게이션 컨트롤러를 생성하여 화면 전환을 관리
                NavHost(//네비게이션 그래프를 정의하고, 화면 간 이동을 가능하게 함
                    navController = navController,
                    startDestination = "home"//앱이 실행될 때 "home" (HomeScreen) 부터 시작.
                ) {
                    composable("home") { HomeScreen(navController) }//"home" 경로로 이동하면 HomeScreen이 표시됨.
                    composable("page1") { Page1ListScreen() }//"page1" 경로로 이동하면 Page1ListScreen이 표시됨.
                    composable("page2") { Page2GalleryScreen() }//"page2" 경로로 이동하면 Page2GalleryScreen이 표시됨.
                    composable("page3") { Page3HelloScreen() }//"page3" 경로로 이동하면 Page3HelloScreen이 표시됨.
                }
            }
        }
    }
}

//모킹
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController() // NavController 생성
    MyApplicationTheme {
        HomeScreen(navController) // NavController 전달
    }
}