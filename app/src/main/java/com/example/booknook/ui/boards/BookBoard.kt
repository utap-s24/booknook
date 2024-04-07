package com.example.booknook.ui.boards

data class BookBoard(
    val bookBoardId: String,
    val isPublic: Boolean,
    val bookBoardTitle: String,
    val numBooksInBoard: Int,
    val booksInBoard: MutableList<SavedBook>,
)