package com.example.booknook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknook.api.Book
import com.example.booknook.api.BookRepository
import com.example.booknook.api.NewYorkTimesAPI
import com.example.booknook.ui.boards.BookBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val apiKey = ""
    private val newYorkTimesAPI = NewYorkTimesAPI.create()
    private val newYorkTimesBookRepo = BookRepository(newYorkTimesAPI)
    private val fetchDone = MutableLiveData(false)
    private var netBooks = MutableLiveData<List<Book>>()
    private var booksBookmarked = MutableLiveData<List<Book>>(emptyList())
    private var bookBoardsList = MutableLiveData<List<BookBoard>>(mutableListOf(
        BookBoard(0, "Gothic Fantasy", 0, mutableListOf()),
        BookBoard(1, "Self-Help Improvement", 0, mutableListOf()),
        BookBoard(2, "Enemies to Lovers Romance", 0, mutableListOf()),
        BookBoard(3, "Fantasy TBR", 0, mutableListOf()),))
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
    // BOOKMARKED BOOKS
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
        val id = bookBoardsList.value!!.size
        bookBoardsList.postValue(bookBoardsList.value?.plus(BookBoard(id, title, 0, mutableListOf())))
    }
    fun getBookBoard(id: Int) : BookBoard? {
        return bookBoardsList.value!!.find { it.bookBoardId == id }
    }

    // OBSERVE
    fun observeNetBooks() : LiveData<List<Book>> {
        return netBooks
    }
    fun observeFetchDone() : LiveData<Boolean> {
        return fetchDone
    }
    fun observeBookBoardsList() : LiveData<List<BookBoard>> {
        return bookBoardsList
    }

}