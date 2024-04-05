package com.example.booknook.ui.boards

import com.example.booknook.api.Book

data class BookBoard(
    val bookBoardId: Int,
    val bookBoardTitle: String,
    val numBooksInBoard: Int,
    val booksInBoard: MutableList<Book>,
)