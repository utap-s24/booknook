package com.example.booknook

import android.util.Log
import com.example.booknook.ui.boards.BookBoard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// BASE CODE FROM FLIPPED CLASSROOM #8 PHOTOLIST

class DatabaseViewModel {
    private val db = Firebase.firestore
    private val savedBooksCollection = "Bookmarked Books" // XXX
    private val rootCollection = "bookboards"

    private fun getUserId(): String? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d(javaClass.simpleName, "User must be logged in.")
        }
        return userId
    }
    private fun limitAndGet(query: Query,
                            resultListener: (List<BookBoard>)->Unit) {
        val userId = getUserId()
        query
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "bookboards fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(BookBoard::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "bookboards fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    fun fetchBookBoard(
//        sortInfo: SortInfo,
        resultListener: (List<BookBoard>) -> Unit
    ) {
        val query = db.collection(rootCollection)
        limitAndGet(query, resultListener)
    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createBookBoard(
//        sortInfo: SortInfo,
        bookBoard: BookBoard,
        resultListener: (List<BookBoard>)->Unit
    ) {
        db.collection(rootCollection).add(bookBoard)
            .addOnSuccessListener {
                bookBoard.docId = it.id
                fetchBookBoard(resultListener)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "createBookBoard FAILED ", it)
            }
    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removeBookBoard(
//        sortInfo: SortInfo,
        bookBoard: BookBoard,
        resultListener: (List<BookBoard>)->Unit
    ) {
        db.collection(rootCollection)
            .document(bookBoard.userId)
            .delete()
            .addOnSuccessListener {
                fetchBookBoard(resultListener)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Bookboard deleting FAILED")
            }
    }
}
