package com.example.booknook

import android.util.Log
import com.example.booknook.ui.boards.BookBoard
import com.example.booknook.ui.boards.SavedBook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
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
                // Need to make bookBoards & books in boards a certain format
                val bookBoardList = result.documents.mapNotNull {
                    it.toObject(BookBoard::class.java)?.apply {
                        this.docId = it.id // ensures they all have a docId
                        it.reference.collection("books").get()
                            .addOnSuccessListener { books ->
                                val savedBooksInBoard = books.mapNotNull { book ->
                                    book.toObject(SavedBook::class.java)
                                }
                                this.booksInBoard = savedBooksInBoard.toMutableList()
                            }

                    }
                }
                resultListener(bookBoardList)
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
                fetchBookBoard(resultListener)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "createBookBoard FAILED ", it)
            }
    }

    private fun attachDocIdToBook(
        bookBoardDocId: String,
        bookDocReference : DocumentReference) {
        db.collection(rootCollection)
            .document(bookBoardDocId)
            .collection("books")
            .document(bookDocReference.id)
            .update("docId", bookDocReference.id)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Book id recorded SUCCESSFULLY")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Book id recording FAILED")
            }
    }

    fun addBookToBookBoard(
        bookBoardDocId: String,
        book : SavedBook,
        resultListener: (List<BookBoard>) -> Unit) {
        db.collection(rootCollection)
            .document(bookBoardDocId)
            .collection("books")
            .add(book)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Book successfully added to BookBoard")
                attachDocIdToBook(bookBoardDocId, it)
                fetchBookBoard(resultListener)
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "Error adding book to BookBoard", e)
            }
    }
    fun removeBookFromBookBoard(
        bookBoardDocId: String,
        book : SavedBook,
        resultListener: (List<BookBoard>) -> Unit) {
        val bookDocId = book.docId
        if (bookDocId == null) {
            Log.d(javaClass.simpleName, "invalid book document ID")
            return
        }
        db.collection(rootCollection)
            .document(bookBoardDocId)
            .collection("books")
            .document(bookDocId)
            .delete()
            .addOnSuccessListener {
                fetchBookBoard(resultListener)
                Log.d(javaClass.simpleName, "Book delete SUCCESSFUL")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Book deleting FAILED")
            }
    }
    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removeBookBoard(
//        sortInfo: SortInfo,
        bookBoard: BookBoard,
        resultListener: (List<BookBoard>)->Unit
    ) {
        val docId = bookBoard.docId
        if (docId.isNullOrEmpty()) {
            Log.d(javaClass.simpleName, "Invalid Document Id found. Cannot delete.")
            return
        }
        db.collection(rootCollection)
            .document(docId)
            .delete()
            .addOnSuccessListener {
                fetchBookBoard(resultListener)
                Log.d(javaClass.simpleName, "BookBoard delete SUCCESSFUL")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "BookBoard deleting FAILED")
            }
    }
}
