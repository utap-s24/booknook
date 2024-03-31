package com.example.booknook.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknook.api.Book
import com.example.booknook.api.BookRepository
import com.example.booknook.api.NewYorkTimesAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    private val newYorkTimesAPI = NewYorkTimesAPI.create()
    private val newYorkTimesBookRepo = BookRepository(newYorkTimesAPI)
    private val fetchDone = MutableLiveData(false)

    private var netBooks = MediatorLiveData<List<Book>>().apply {
            // XXX Write me, viewModelScope.launch getPosts
        viewModelScope.launch (context = viewModelScope.coroutineContext + Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                // BOOKNOOK API KEY
                value = newYorkTimesBookRepo.getBooks("")
                fetchDone.postValue(true)
            }
        }
    }
    fun observeNetBooks() : LiveData<List<Book>> {
        return netBooks
    }
}