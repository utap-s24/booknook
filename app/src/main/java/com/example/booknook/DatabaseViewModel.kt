package com.example.booknook

import android.util.Log
import com.example.booknook.ui.boards.BookBoard
import com.example.booknook.ui.boards.SavedBook
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// BASE CODE FROM FLIPPED CLASSROOM #8 PHOTOLIST

class DatabaseViewModel {
    private val db = Firebase.firestore
    private val savedBooksCollection = "Bookmarked Books" // XXX
    private val rootCollection = "bookboards"
    private val profileCollection = "users"

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
                val pendingOperations = mutableListOf<Task<*>>()

                val bookBoardList = result.documents.mapNotNull {
                    it.toObject(BookBoard::class.java)?.apply {
                        this.docId = it.id // ensures they all have a docId
                        val booksFetch = it.reference.collection("books").get()
                        pendingOperations.add(
                            booksFetch.addOnSuccessListener { books ->
                                val savedBooksInBoard = books.mapNotNull { book ->
                                    book.toObject(SavedBook::class.java)
                                }
                                this.booksInBoard = savedBooksInBoard.toMutableList()
                                println("books recorded: $booksInBoard")
                            }
                        )
                    }
                }
                Tasks.whenAllComplete(pendingOperations).addOnCompleteListener {
                    resultListener(bookBoardList)
                }
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
        resultListener: (List<BookBoard>) -> Unit
    ) {
        val query = db.collection(rootCollection)
        limitAndGet(query, resultListener)
    }

    fun fetchBooks(
        bookBoard: BookBoard,
        resultListener: (List<SavedBook>)->Unit
    ) {
        val docId = bookBoard.docId ?: return

        db.collection(rootCollection)
            .document(docId)
            .collection("books")
            .get()
            .addOnSuccessListener { result ->
               val books = result.documents.mapNotNull { book ->
                   book.toObject(SavedBook::class.java)
               }
                resultListener(books)
                Log.d(javaClass.simpleName, "books fetch SUCCESS")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "books fetch FAILED ", it)
            }
    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createBookBoard(
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
    fun updateBookBoardPublicStatus(bookBoard: BookBoard) {
        val bookBoardDocId = bookBoard.docId
        if (bookBoardDocId.isNullOrEmpty()) {
            Log.d(javaClass.simpleName, "Bookboard's document ID is invalid.")
            return
        }

        db.collection(rootCollection)
            .document(bookBoardDocId)
            .update("public", bookBoard.isPublic)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "BookBoard public status update recorded SUCCESSFULLY")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "BookBoard public status update recorded FAILURE")
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

    private fun attachDocIdToSavedBook(
        bookDocReference : DocumentReference,
        resultListener: (List<SavedBook>) -> Unit
    ) {
        db.collection(savedBooksCollection)
            .document(bookDocReference.id)
            .update("docId", bookDocReference.id)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Saved Book id recorded SUCCESSFULLY")
                fetchSavedBooks(resultListener)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Saved Book id recording FAILED")
            }
    }

    fun addToBookmarkedBooks(
        book : SavedBook,
        resultListener: (List<SavedBook>) -> Unit
    ) {
        db.collection(savedBooksCollection)
            .add(book)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Book successfully added to Saved Books")
                attachDocIdToSavedBook(it, resultListener)

            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Book add FAILED")

            }
    }

    fun removeFromBookmarkedBooks(
        book: SavedBook,
        resultListener: (List<SavedBook>) -> Unit
    ) {
        val bookDocId = book.docId
        if (bookDocId.isNullOrEmpty()) {
            Log.d(javaClass.simpleName, "Invalid or missing book document ID")
            return
        }
        Log.d(javaClass.simpleName, "Attempting to delete document with ID: $bookDocId")
        db.collection(savedBooksCollection)
            .document(bookDocId)
            .delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Book successfully removed from Saved Books")
                fetchSavedBooks(resultListener)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Book remove FAILED")

            }
    }

    fun fetchSavedBooks(
        resultListener: (List<SavedBook>) -> Unit
    ) {
        val userId = getUserId()
        db.collection(savedBooksCollection)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val books = result.documents.mapNotNull { book ->
                    book.toObject(SavedBook::class.java)
                }
                println(books)
                resultListener(books)
                Log.d(javaClass.simpleName, "books fetch SUCCESS")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "books fetch FAILED ", it)
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

    // PROFILE PERSONAL INFORMATION

    fun saveDisplayName(name : String) {
        Log.d(javaClass.simpleName, "saving displayName")
        val userDoc = db.collection(profileCollection).document(getUserId()!!)
        userDoc.set(hashMapOf("displayName" to name), SetOptions.merge()) // AI CONTRIBUTION
    }

    // AI CONTRIBUTION
    fun fetchDisplayName(onSuccess: (String) -> Unit) {
        val userId = getUserId()
        val userDocRef = db.collection(profileCollection).document(userId!!)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val displayName = documentSnapshot.getString("displayName")
                    if (displayName != null) {
                        onSuccess(displayName)
                    } else {

                    }
                } else {

                }
            }
            .addOnFailureListener { e ->

            }
    }

    fun saveBio(bio : String) {
        Log.d(javaClass.simpleName, "saving displayName")
        val userDoc = db.collection(profileCollection).document(getUserId()!!)
        userDoc.set(hashMapOf("aboutMe" to bio), SetOptions.merge()) // AI CONTRIBUTION
    }

    // AI CONTRIBUTION
    fun fetchBio(onSuccess: (String) -> Unit) {
        val userId = getUserId()
        val userDocRef = db.collection(profileCollection).document(userId!!)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val bio = documentSnapshot.getString("aboutMe")
                    if (bio != null) {
                        onSuccess(bio)
                    } else {

                    }
                } else {

                }
            }
            .addOnFailureListener { e ->

            }
    }
}
