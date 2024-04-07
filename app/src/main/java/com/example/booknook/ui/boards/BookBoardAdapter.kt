package com.example.booknook.ui.boards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.MainViewModel
import com.example.booknook.databinding.BookItemInBoardBinding
import com.example.booknook.glide.Glide

class BookBoardAdapter(private val viewModel: MainViewModel)
    : ListAdapter<SavedBook, BookBoardAdapter.VH>(BookBoardAdapter.SavedBookDiff()) {

    inner class VH(private val bookItemInBoardBinding: BookItemInBoardBinding) : RecyclerView.ViewHolder(bookItemInBoardBinding.root) {
        fun bind(book: SavedBook) {
            Glide.glideFetch(
                book.imageUrl,
                bookItemInBoardBinding.bookCover
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookBoardAdapter.VH {
        val binding = BookItemInBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: BookBoardAdapter.VH, position: Int) {
        holder.bind(getItem(position))
    }

    class SavedBookDiff : DiffUtil.ItemCallback<SavedBook>() {
        override fun areItemsTheSame(oldItem: SavedBook, newItem: SavedBook): Boolean {
            return oldItem.bookId == newItem.bookId
        }
        // XXX may need to compare books in board
        override fun areContentsTheSame(oldItem: SavedBook, newItem: SavedBook): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.ibsn10 == newItem.ibsn10 &&
                    oldItem.ibsn13 == newItem.ibsn13
        }
    }

    }