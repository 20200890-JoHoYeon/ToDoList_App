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

        // Firestore에 저장할 객체
        val firestoreItem = hashMapOf(
            "documentId" to item.documentId,
            "title" to item.title,
            "content" to item.content,
            "date" to item.date,
            "isCompleted" to item.isCompleted
        )

        // 문서가 존재하면 업데이트, 존재하지 않으면 새로 추가
        itemRef.document(item.documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 기존 문서가 존재하면 update()로 필드만 수정
                    itemRef.document(item.documentId)
                        .update(
                            "title", item.title,
                            "content", item.content,
                            "date", item.date,
                            "isCompleted", item.isCompleted
                        )
                        .addOnSuccessListener {
                            Log.d("Firestore", "Item updated successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error updating item", e)
                        }
                } else {
                    // 문서가 존재하지 않으면 새로 추가
                    itemRef.document(item.documentId)
                        .set(firestoreItem, SetOptions.merge()) // 기존 필드만 병합하여 저장
                        .addOnSuccessListener {
                            Log.d("Firestore", "Item saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error saving item", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking document existence", e)
            }
    }


    fun deleteItemFromFirestore(documentId: String, userId: String) {
        val itemRef = db.collection("users").document(userId).collection("items").document(documentId)

        itemRef.delete()
            .addOnSuccessListener { Log.d("Firestore", "Item deleted successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error deleting item", e) }
    }

    // 클라우드 디비에서 불러온 아이템을 추가하거나 업데이트하는 함수
    fun insertOrUpdateItems(items: List<Item>) {
        // Room DB에 삽입 또는 업데이트
        viewModelScope.launch {
            try {
                items.forEach { item ->
                    Log.d("RoomDB", "Inserting or updating item: ${item.title}")
                }
                repository.insertItems(items) // Room DB에 삽입 또는 업데이트
                Log.d("RoomDB", "Items inserted/updated successfully")
            } catch (e: Exception) {
                Log.e("RoomDB", "Error inserting/updating items", e)
            }
        }
    }

}