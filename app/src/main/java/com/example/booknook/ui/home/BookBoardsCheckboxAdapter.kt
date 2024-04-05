package com.example.booknook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.R
import com.example.booknook.databinding.BookBoardCheckRowBinding
import com.example.booknook.glide.Glide
import com.example.booknook.ui.boards.BookBoard

class BookBoardsCheckboxAdapter(private val viewModel: HomeViewModel,
                                private val addBookToBoard : (BookBoard) -> Unit)
    : ListAdapter<BookBoard, BookBoardsCheckboxAdapter.VH>(BookBoardsCheckboxAdapter.BookBoardDiff()) {

    inner class VH(private val bookBoardCheckRowBinding: BookBoardCheckRowBinding) : RecyclerView.ViewHolder(bookBoardCheckRowBinding.root) {
        fun bind(bookBoard: BookBoard) {
            bookBoardCheckRowBinding.BoardTitle.text = bookBoard.bookBoardTitle
            if (bookBoard.booksInBoard.isNotEmpty()) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[0].imageUrl,
                    bookBoardCheckRowBinding.bookPreview1
                )
            } else {
                bookBoardCheckRowBinding.bookPreview1.visibility = View.INVISIBLE
            }
            if (bookBoard.booksInBoard.size >= 2) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[1].imageUrl,
                    bookBoardCheckRowBinding.bookPreview2
                )
            }
            else {
                bookBoardCheckRowBinding.bookPreview2.visibility = View.INVISIBLE
            }

            if (bookBoard.booksInBoard.size >= 3) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[2].imageUrl,
                    bookBoardCheckRowBinding.bookPreview3
                )
            } else {
                bookBoardCheckRowBinding.bookPreview3.visibility = View.INVISIBLE
            }
            bookBoardCheckRowBinding.checkbox.setOnClickListener {
                bookBoardCheckRowBinding.checkbox.setImageResource(R.drawable.baseline_check_circle_24)
                addBookToBoard(bookBoard)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = BookBoardCheckRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class BookBoardDiff : DiffUtil.ItemCallback<BookBoard>() {
        override fun areItemsTheSame(oldItem: BookBoard, newItem: BookBoard): Boolean {
            return oldItem.bookBoardId == newItem.bookBoardId
        }
        // XXX may need to compare books in board
        override fun areContentsTheSame(oldItem: BookBoard, newItem: BookBoard): Boolean {
            return oldItem.bookBoardTitle == newItem.bookBoardTitle &&
                    oldItem.numBooksInBoard == newItem.numBooksInBoard
        }
    }
}