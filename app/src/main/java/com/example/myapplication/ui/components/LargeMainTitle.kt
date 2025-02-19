package com.example.myapplication.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun LargeMainTitle(
    fontSize: Int,
    text: String,
    fontWeight: FontWeight = FontWeight.Bold // Default value set to Normal
) {
    Text(
        text = text, // Using the passed text
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium,
        fontSize = fontSize.sp,
        fontWeight = fontWeight
    )
}