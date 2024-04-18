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
    private var booksBookmarked = MutableLiveData<List<SavedBook>>(emptyList())
    private var bookBoardsList = MutableLiveData<List<BookBoard>>(mutableListOf())
    private var bookListForOneBoardFragment = MutableLiveData<List<SavedBook>>()
    private var userBio = MutableLiveData<String>()
    private var displayName = MutableLiveData<String>()

    private fun netRefresh() {
        fetchDone.postValue(false)
        viewModelScope.launch (context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
            netBooks.postValue(newYorkTimesBookRepo.getBooks(apiKey))
            fetchDone.postValue(true)
        }
    }
    // FIREBASE
    fun initProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            netRefresh()
            dbViewModel.fetchSavedBooks {
                booksBookmarked.postValue(it)
            }
            username.postValue(user.email)
            dbViewModel.fetchDisplayName{
                displayName.postValue(it)
            }
            dbViewModel.fetchBookBoard {
                bookBoardsList.postValue(it)
            }
            dbViewModel.fetchBio {
                userBio.postValue(it)
            }
        }
    }
    fun searchGoogleBooks(search : String) {
        if (search.isEmpty())
            netBooks.postValue(netBooks.value)
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

    fun getSavedBooksAsBoard() : BookBoard {
        val books = mutableListOf<SavedBook>()
        booksBookmarked.value?.map { book ->
            books.add(book)
        }
        return BookBoard("", "", false, "Bookmarked", books)
    }
    fun addBookToBookmarkedList(book: Book) {
        val authors = book.author.split(',')
        val savedBook =  FirebaseAuth.getInstance().currentUser?.uid?.let {SavedBook("", it, book.title, authors, book.imageUrl, book.isbn10, book.isbn13) }
            ?: return
        dbViewModel.addToBookmarkedBooks(savedBook) {
            booksBookmarked.postValue(it)
        }
    }
    fun removeBookFromBookmarkedList(bookReg : Book) {
        val savedBook = booksBookmarked.value?.find {
            bookReg.isbn10 == it.isbn10 &&
            bookReg.isbn13 == it.isbn13 }
        if (savedBook != null) {
            dbViewModel.removeFromBookmarkedBooks(savedBook) {
                booksBookmarked.postValue(it)
            }
        }
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
        if (bookBoard.docId.isNullOrEmpty()) {
            dbViewModel.removeFromBookmarkedBooks(book) {
                booksBookmarked.postValue(it)
            }
            return
        }
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
    fun setSavedBooksInOneBoardFragment() {
        bookListForOneBoardFragment.postValue(booksBookmarked.value)
    }

    // PROFILE
    fun updateBio(bio : String) {
        dbViewModel.saveBio(bio)
        userBio.postValue(bio)
    }

    fun updateDisplayName(name : String) {
        dbViewModel.saveDisplayName(name)
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

    fun getUsername() : String {
        return username.value!!
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
    fun observeBooksBookmarked() : LiveData<List<SavedBook>> {
        return booksBookmarked
    }

}
