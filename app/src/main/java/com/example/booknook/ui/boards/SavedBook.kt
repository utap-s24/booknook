package com.example.booknook.ui.boards

data class SavedBook(
    val bookId: String,
    val title: String,
    val authors: List<String>,
    val imageUrl: String,
    val ibsn10: String,
    val ibsn13: String
)