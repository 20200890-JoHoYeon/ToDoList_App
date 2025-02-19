package com.example.myapplication.model

import android.os.Build
import androidx.annotation.RequiresApi

data class ItemData @RequiresApi(Build.VERSION_CODES.O) constructor(var title: String, var content: String, var date: String)