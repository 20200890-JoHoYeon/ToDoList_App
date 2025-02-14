package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.Page1ListScreen
import com.example.myapplication.ui.screens.Page2GalleryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HomeScreen(
                    navigateToPage1 = { startActivity(Intent(this, Page1ListScreen::class.java)) },
                    navigateToPage2 = { startActivity(Intent(this, Page2GalleryScreen::class.java)) }

                )
            }
        }
    }
}






@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        HomeScreen({}, {})
    }
}
