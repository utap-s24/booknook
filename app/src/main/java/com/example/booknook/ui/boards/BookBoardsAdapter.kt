package com.example.booknook.ui.boards

import com.example.booknook.ui.home.HomeViewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.databinding.BookBoardRowBinding
import com.example.booknook.glide.Glide

class BookBoardsAdapter(private val viewModel: HomeViewModel)
    : ListAdapter<BookBoard, BookBoardsAdapter.VH>(BookBoardDiff()) {

    inner class VH(private val bookBoardRowBinding: BookBoardRowBinding) : RecyclerView.ViewHolder(bookBoardRowBinding.root) {
        fun bind(bookBoard: BookBoard) {
            bookBoardRowBinding.BoardTitle.text = bookBoard.bookBoardTitle
            if (bookBoard.booksInBoard.isNotEmpty()) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[0].imageUrl,
                    bookBoardRowBinding.bookPreview1
                )
            } else {
                bookBoardRowBinding.bookPreview1.visibility = View.INVISIBLE
            }
            if (bookBoard.booksInBoard.size >= 2) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[1].imageUrl,
                    bookBoardRowBinding.bookPreview2
                )
            }
            else {
                bookBoardRowBinding.bookPreview2.visibility = View.INVISIBLE
            }

            if (bookBoard.booksInBoard.size >= 3) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[2].imageUrl,
                    bookBoardRowBinding.bookPreview3
                )
            } else {
                bookBoardRowBinding.bookPreview3.visibility = View.INVISIBLE
            }
            if (bookBoard.booksInBoard.size >= 4) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[3].imageUrl,
                    bookBoardRowBinding.bookPreview4
                )
            } else {
                bookBoardRowBinding.bookPreview4.visibility = View.INVISIBLE
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = BookBoardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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