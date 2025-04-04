package com.hottak.todoList.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    fun deleteItems() {
        viewModelScope.launch {
            try {
                repository.deleteItems()
                Log.d("RoomDB", "Items deleted successfully") // 수정된 로그 메시지
            } catch (e: Exception) {
                Log.e("RoomDB", "Error deleting items", e) // 수정된 로그 메시지
            }
        }
    }


    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun updateItem(item: Item, userId: String) {
        viewModelScope.launch {
            // Room DB 업데이트
            repository.updateItem(item)

            // Firestore 업데이트
            saveItemToFirestore(item, userId)  // userId는 현재 로그인한 사용자 ID
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
                            "documentId", item.documentId,
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

    //클라우드 DB 단일 삭제
    fun deleteItemFromFirestore(documentId: String, userId: String) {
        val itemRef = db.collection("users").document(userId).collection("items").document(documentId)

        itemRef.delete()
            .addOnSuccessListener { Log.d("Firestore", "Item deleted successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error deleting item", e) }
    }
    //클라우드 DB 전체 삭제
    fun deleteAllItemsFromFirestore(userId: String) {
        val itemsCollection = db.collection("users").document(userId).collection("items")
        itemsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
                Log.d("Firestore", "All items deleted from Firestore successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting all items", e)
            }
    }

    //클라우드 계정 삭제
    fun deleteUserAccount(userId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.uid == userId) {
            // 1. Firestore에서 사용자 데이터 삭제
            val userRef = db.collection("users").document(userId)
            userRef.delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "User data deleted from Firestore")

                    viewModelScope.launch {
                        // 3. Firebase Authentication에서 사용자 계정 삭제
                        currentUser.delete()
                            .addOnSuccessListener {
                                Log.d("FirebaseAuth", "User account deleted successfully!")
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirebaseAuth", "Error deleting user account", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error deleting user data from Firestore", e)
                }
        } else {
            Log.e("FirebaseAuth", "No authenticated user found or userId mismatch")
        }
    }

    fun fetchDataFromFirestore(userId: String) {

        // Firestore의 users/{userId}/items 경로에서 데이터 가져오기
        val itemsRef = db.collection("users").document(userId).collection("items")

        Log.d("Firestore", "Fetching data for userId: $userId")

        itemsRef.get()
            .addOnSuccessListener { documents ->
                Log.d("Firestore", "Data fetch successful!")
                val itemsList = mutableListOf<Item>()
                for (document in documents) {
                    try {
                        // Firestore에서 ItemData로 변환
                        val firestoreItem = ItemData(
                            documentId = document.getString("documentId") ?: "",
                            title = document.getString("title") ?: "",
                            content = document.getString("content") ?: "",
                            date = document.getString("date") ?: "",
                            isCompleted = document.getBoolean("isCompleted") ?: false
                        )

                        Log.d("Firestore", "Fetched item: Title = ${firestoreItem.title}, Content = ${firestoreItem.content}, Date = ${firestoreItem.date}, Completed = ${firestoreItem.isCompleted}")

                        // 아이템 추가
                        itemsList.add(firestoreItem.toItem())
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error processing document: ${document.id}", e)
                    }
                }

                // Firestore에서 데이터를 가져온 후 Room DB에 저장
                if (itemsList.isNotEmpty()) {
                    Log.d("Firestore", "Inserting ${itemsList.size} items into Room DB.")
                    insertOrUpdateItems(itemsList) // 여러 아이템을 한 번에 저장
                } else {
                    Log.d("Firestore", "No items found in Firestore.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching items: ${e.message}", e)
            }
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