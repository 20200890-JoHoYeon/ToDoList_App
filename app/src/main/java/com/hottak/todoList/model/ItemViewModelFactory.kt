package com.hottak.todoList.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ItemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
                // If the modelClass is ItemViewModel, create and return it
                ItemViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        } catch (e: Exception) {
            // Log the exception for debugging
            Log.e("ItemViewModelFactory", "Error creating ViewModel: ${e.message}")

            // You can throw the exception again to propagate it or handle it
            throw e  // Optionally, you can rethrow the exception to notify the caller of failure
        }
    }
}
