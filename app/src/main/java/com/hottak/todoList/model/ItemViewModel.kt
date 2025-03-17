package com.hottak.todoList.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // Firestore 인스턴스 추가

    val all: LiveData<List<ItemData>>
    val allItems: LiveData<List<ItemData>>
    val allCompletedItems: LiveData<List<ItemData>>

    init {
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
        all = repository.all
        allItems = repository.allItems
        allCompletedItems = repository.allCompletedItems
    }

    fun insertItem(item: Item) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun saveItemToFirestore(item: Item, userId: String) {
        val itemRef = db.collection("users").document(userId).collection("items")
        val firestoreItem = FireStoreItemData(item.title, item.content, item.date, item.isCompleted)

        itemRef.document(item.id.toString()) // RoomDB ID 기반
            .set(firestoreItem)
            .addOnSuccessListener { Log.d("Firestore", "Item saved successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error saving item", e) }
    }

    fun deleteItemFromFirestore(item: Item, userId: String) {
        val itemRef = db.collection("users").document(userId).collection("items").document(item.id.toString())

        itemRef.delete()
            .addOnSuccessListener { Log.d("Firestore", "Item deleted successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error deleting item", e) }
    }


    fun insertItems(itemsList: List<Item>) {
        viewModelScope.launch {
            // Room DB에 여러 아이템을 삽입
            repository.insertItems(itemsList)
        }
    }
}