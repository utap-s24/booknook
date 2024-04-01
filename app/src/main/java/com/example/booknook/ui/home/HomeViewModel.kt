package com.example.booknook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknook.api.Book
import com.example.booknook.api.BookRepository
import com.example.booknook.api.NewYorkTimesAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val apiKey = "wwz1OYAA50xYPs10UzXGanP7z1fN2v0a"
    private val newYorkTimesAPI = NewYorkTimesAPI.create()
    private val newYorkTimesBookRepo = BookRepository(newYorkTimesAPI)
    private val fetchDone = MutableLiveData(false)
    private var netBooks = MutableLiveData<List<Book>>()
    private var booksBookmarked = MutableLiveData<List<Book>>(emptyList())
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

    // OBSERVE
    fun observeNetBooks() : LiveData<List<Book>> {
        return netBooks
    }
    fun observeFetchDone() : LiveData<Boolean> {
        return fetchDone
    }

}