package com.example.booknook.ui.boards

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class SavedBook(
    var docId: String? = null,
    @PropertyName("title")
    val title: String = "",
    @PropertyName("authors")
    val authors: List<String> = listOf(),
    @PropertyName("imageUrl")
    val imageUrl: String = "",
    @PropertyName("isbn10")
    val isbn10: String = "",
    @PropertyName("isbn13")
    val isbn13: String = ""

) : Serializable