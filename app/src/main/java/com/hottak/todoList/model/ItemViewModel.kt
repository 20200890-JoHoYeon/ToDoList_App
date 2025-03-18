package com.hottak.todoList.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
        val firestoreItem = ItemData(item.documentId, item.title, item.content, item.date, item.isCompleted)

        itemRef.document(item.documentId) // Room DB의 id 값 사용
            .set(firestoreItem, SetOptions.merge()) // 필드만 병합하고 덮어쓰지 않음
            .addOnSuccessListener { Log.d("Firestore", "Item saved successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error saving item", e) }
    }

    fun deleteItemFromFirestore(documentId: String, userId: String) {
        val itemRef = db.collection("users").document(userId).collection("items").document(documentId)

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