package com.example.booknook.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the profile Fragment"
    }
    val text: LiveData<String> = _text

    private val username = MutableLiveData<String>()

    fun updateUsername() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            username.postValue(user.email)
        }
    }

    fun observeUsername() : MutableLiveData<String> {
        return username
    }
}