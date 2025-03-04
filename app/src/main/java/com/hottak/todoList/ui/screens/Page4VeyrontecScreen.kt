//package com.hottak.todoList.ui.screens
//
//import android.content.Context
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavHostController
//import com.hottak.todoList.ui.components.TopBar
//import java.io.BufferedReader
//import java.io.InputStreamReader
//
//@Composable
//fun Page4VeyrontecScreen(navController: NavHostController) {
//    val context = LocalContext.current
//    Scaffold(
//        containerColor = Color.White,
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = Color.White),
//        topBar = { TopBar() },
//        bottomBar = {
//
//        },
//        content = { innerPadding ->
//            VeyrontecContent(
//                context = context,
//                innerPadding = innerPadding,
//            )
//        }
//    )
//}
//
//@Composable
//fun VeyrontecContent(innerPadding: PaddingValues, context: Context) {
//    val deviceNo = readSettingIni(context, "Setting_Sample.ini")
//    Column(modifier = Modifier.padding(innerPadding)) {
//        Text(text = "[Todo_List]")
//        if (!deviceNo.isNullOrEmpty()) {
//            DeviceNoDisplay(deviceNo)
//        } else {
//            DeviceNoDisplay("No Device No Found")
//        }
//    }
//}
//
//fun readSettingIni(context: Context, fileName: String): String? {
//    try {
//        val inputStream = context.assets.open(fileName)
//        val reader = BufferedReader(InputStreamReader(inputStream))
//        var line: String?
//        var inShadsSection = false
//        var deviceNo: String? = null
//
//        while (reader.readLine().also { line = it } != null) {
//            line = line?.trim()
//
//            if (line?.startsWith("[") == true && line?.endsWith("]") == true) {
//                // Check if we're entering the [SHADS] section
//                inShadsSection = line == "[SHADS]"
//            } else if (inShadsSection) {
//                // If we're in the [SHADS] section, look for the device number
//                if (line?.startsWith("STRING_DEVICE_NUMBER=") == true) {
//                    deviceNo = line?.substringAfter("=")?.trim()
//                    break // Found it, no need to continue
//                }
//            }
//        }
//        reader.close()
//        inputStream.close()
//
//        Log.d("INI_Read", "Read STRING_DEVICE_NUMBER: $deviceNo")
//        return deviceNo
//    } catch (e: Exception) {
//        Log.e("INI_Read_Error", "Error reading the INI file", e)
//        return null
//    }
//}
//
//@Composable
//fun DeviceNoDisplay(deviceNo: String) {
//    // 화면에 Device_NO 출력
//    Text(text = "Device_NO: $deviceNo")
//}