package com.example.booknook.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BoardsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is boards Fragment"
    }
    val text: LiveData<String> = _text
}