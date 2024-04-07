package com.example.booknook

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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val apiKey = "Ss5agscGIFD0Sv7es8krx0AA0aPhc95G"
    private val db = Firebase.firestore
    // auth
    private val username = MutableLiveData<String>()

    private val newYorkTimesAPI = NewYorkTimesAPI.create()
    private val newYorkTimesBookRepo = BookRepository(newYorkTimesAPI)
    private val googleBooksAPI = GoogleBooksAPI.create()
    private val googleBooksRepo = GoogleBooksRepository(googleBooksAPI)
    private val fetchDone = MutableLiveData(false)
    private var netBooks = MutableLiveData<List<Book>>()
    private var netSearchBooks = MutableLiveData<List<GoogleBook>>()
    private var booksBookmarked = MutableLiveData<List<Book>>(emptyList())
    private var bookBoardsList = MutableLiveData<List<BookBoard>>(mutableListOf(
        BookBoard("0", true, "Gothic Fantasy", 0, mutableListOf()),
        BookBoard("1", true, "Self Help & Lifestyle", 0, mutableListOf()),
                BookBoard("2", true, "Friends to Lovers Romance", 0, mutableListOf())
        )
    )
    init {
        netRefresh()
    }
    fun netRefresh() {
        fetchDone.postValue(false)
        viewModelScope.launch (context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
            netBooks.postValue(newYorkTimesBookRepo.getBooks(apiKey))
            fetchDone.postValue(true)
        }
    }
    fun updateUsername() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            username.postValue(user.email)
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
    fun fetchSavedBooks() {
        db.collection("Bookmarked Books").document("yourDocumentId")

    }
    fun isSaved(book: Book): Boolean {
        if (booksBookmarked.value == null)
            return false
        return booksBookmarked.value!!.contains(book)
    }
    fun addBookToBookmarkedList(book: Book) {
        booksBookmarked.postValue(booksBookmarked.value?.plus(book))
    }
    fun removeBookFromBookmarkedList(book: Book) {
        booksBookmarked.value?.filter { book != it }
    }
    // BOOK BOARDS
    fun createBookBoard(title : String) {
        // XXX get doc ID
        bookBoardsList.postValue(bookBoardsList.value?.plus(BookBoard("", true, title, 0, mutableListOf())))
    }
    fun getBookBoard(id: String) : BookBoard? {
        return bookBoardsList.value!!.find { it.bookBoardId == id }
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

}
