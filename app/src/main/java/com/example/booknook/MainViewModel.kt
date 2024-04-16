package com.example.booknook

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknook.api.Book
import com.example.booknook.api.BookRepository
import com.example.booknook.api.GoogleBook
import com.example.booknook.api.GoogleBooksAPI
import com.example.booknook.api.GoogleBooksRepository
import com.example.booknook.api.NewYorkTimesAPI
import com.example.booknook.ui.boards.BookBoard
import com.example.booknook.ui.boards.SavedBook
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val apiKey = ""
    private val username = MutableLiveData<String>()
    private val dbViewModel = DatabaseViewModel()

    private val newYorkTimesAPI = NewYorkTimesAPI.create()
    private val newYorkTimesBookRepo = BookRepository(newYorkTimesAPI)
    private val googleBooksAPI = GoogleBooksAPI.create()
    private val googleBooksRepo = GoogleBooksRepository(googleBooksAPI)
    private val fetchDone = MutableLiveData(false)
    private var netBooks = MutableLiveData<List<Book>>()
    private var netSearchBooks = MutableLiveData<List<GoogleBook>>()
    private var booksBookmarked = MutableLiveData<List<Book>>(emptyList())
    private var bookBoardsList = MutableLiveData<List<BookBoard>>(mutableListOf())
    private var bookListForOneBoardFragment = MutableLiveData<List<SavedBook>>()
    private var userBio = MutableLiveData<String>()
    private var displayName = MutableLiveData<String>()
    init {
        netRefresh()
        dbViewModel.fetchBookBoard {
            bookBoardsList.postValue(it)
        }
    }
    fun netRefresh() {
        fetchDone.postValue(false)
        viewModelScope.launch (context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
            netBooks.postValue(newYorkTimesBookRepo.getBooks(apiKey))
            fetchDone.postValue(true)
        }
    }
    // FIREBASE
    fun initUsername() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            username.postValue(user.email)
            displayName.postValue(user.email)
        }
    }
    fun searchGoogleBooks(search : String) {
        if (search.isEmpty())
            netRefresh()
        else {
            fetchDone.postValue(false)
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO
            ) {
                netSearchBooks.postValue(googleBooksRepo.getBooksFromSearch(search))
                fetchDone.postValue(true)
            }
        }
    }

    fun transformGoogleBookIntoBook(googleBooks: List<GoogleBook>): List<Book> {
        return googleBooks.filter { googleBook ->
            // Check if isbn is not null and then ensure both ISBN_10 and ISBN_13 are present
            googleBook.isbn != null
                    && googleBook.isbn.any { it.isbnType == "ISBN_10" }
                    && googleBook.isbn.any { it.isbnType == "ISBN_13" }
        }.map { googleBook ->
            // It's safe to proceed with transformation since isbn is not null and there are isbn numbers
            Book(
                genres = googleBook.genres,
                subtitle = googleBook.subtitle,
                amazonProductUrl = "",
                title = googleBook.title,
                author = googleBook.authors?.joinToString(", ") ?: "",
                description = googleBook.description ?: "",
                imageUrl = googleBook.imageData?.bookCover ?: "",
                bookImageWidth = 0,
                bookImageHeight = 0,
                publisher = "",
                isbn10 = googleBook.isbn?.find { it.isbnType == "ISBN_10" }?.identifier ?: "",
                isbn13 = googleBook.isbn?.find { it.isbnType == "ISBN_13" }?.identifier ?: "",
                nytRank = 0,
                buyLinks = emptyList()
            )
        }
    }
    // BOOKMARKED BOOKS
    fun isSaved(isbn10: String, isbn13: String): Boolean {
        booksBookmarked.value?.let { books ->
            return books.any { book ->
                book.isbn10 == isbn10 && book.isbn13 == isbn13
            }
        }
        return false
    }
    fun addBookToBookmarkedList(book: Book) {
        booksBookmarked.postValue(booksBookmarked.value?.plus(book))
    }
    fun removeBookFromBookmarkedList(isbn10: String, isbn13: String) {
        val updatedList = booksBookmarked.value!!.filterNot {
            it.isbn10 == isbn10 || it.isbn13 == isbn13
        }
        booksBookmarked.postValue(updatedList)
    }
    // BOOK BOARDS
    fun createBookBoard(title : String, public : Boolean) {
        val newBoard = FirebaseAuth.getInstance().currentUser?.uid?.let { BookBoard("", it, public, title, mutableListOf()) }
        if (newBoard == null) {
            Log.d(javaClass.simpleName, "User must be logged in to create a BookBoard.")
            return
        }
        dbViewModel.createBookBoard(newBoard) {
            bookBoardsList.postValue(it)
        }
    }
    fun addBookToBookBoard(bookBoard: BookBoard, book : SavedBook) {
        if (bookBoard.docId == null)
            return
        dbViewModel.addBookToBookBoard(bookBoard.docId!!, book) {
            bookBoardsList.postValue(it)
        }
    }
    fun deleteBookBoard(bookBoard: BookBoard) {
        dbViewModel.removeBookBoard(bookBoard) {
            bookBoardsList.postValue(it)
        }
    }
    fun deleteBookFromBookBoard(bookBoard: BookBoard, book : SavedBook) {
        if (bookBoard.docId == null)
            return
        dbViewModel.removeBookFromBookBoard(bookBoard.docId!!, book) {
            bookBoardsList.postValue(it)
            dbViewModel.fetchBooks(bookBoard) {
                bookListForOneBoardFragment.postValue(it)
            }
        }

    }
    fun getBookBoard(id: String) : BookBoard? {
        return bookBoardsList.value!!.find { it.docId == id }
    }
    fun updateBookBoardPublicStatus(bookBoard: BookBoard) {
        dbViewModel.updateBookBoardPublicStatus(bookBoard)
    }
    // ONE BOARD FRAGMENT
    fun setBooksInOneBoardFragment(books : List<SavedBook>) {
        bookListForOneBoardFragment.postValue(books)
    }

    // PROFILE

    fun updateBio(bio : String) {
        userBio.postValue(bio)
    }

    fun updateDisplayName(name : String) {
        displayName.postValue(name)
    }

    // OBSERVE
    fun observeNetBooks() : LiveData<List<Book>> {
        return netBooks
    }
    fun observeNetSearchBooks() : LiveData<List<GoogleBook>> {
        return netSearchBooks
    }
    fun observeFetchDone() : LiveData<Boolean> {
        return fetchDone
    }
    fun observeBookBoardsList() : LiveData<List<BookBoard>> {
        return bookBoardsList
    }
    fun observeUsername() : MutableLiveData<String> {
        return username
    }

    fun observeBio() : MutableLiveData<String> {
        return userBio
    }
    fun observeDisplayName() : MutableLiveData<String> {
        return displayName
    }
    fun observeBooksInOneBoardFragment(): LiveData<List<SavedBook>> {
        return bookListForOneBoardFragment
    }

}
