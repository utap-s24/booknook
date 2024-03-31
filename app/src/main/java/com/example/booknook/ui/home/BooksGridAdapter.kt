package com.example.booknook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.R
import com.example.booknook.api.Book
import com.example.booknook.databinding.BookItemBinding

class BookGridAdapter(private val viewModel: HomeViewModel) : ListAdapter<Book, BookGridAdapter.VH>(BookDiff()) {

    inner class VH(private val bookItemBinding: BookItemBinding) : RecyclerView.ViewHolder(bookItemBinding.root) {
        fun bind(bookItem: Book) {
            val layoutParams = bookItemBinding.bookCover.layoutParams
            layoutParams.width = bookItem.bookImageWidth
            layoutParams.height = bookItem.bookImageHeight
            bookItemBinding.bookCover.layoutParams = layoutParams

            bookItemBinding.BookTitle.text = bookItem.title
            bookItemBinding.bookmarkIcon.setOnClickListener {
                bookItemBinding.bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position) )
    }

    class BookDiff : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.isbn13 == newItem.isbn13 &&
                    oldItem.isbn10 == newItem.isbn10
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.author == newItem.author &&
                    oldItem.title == newItem.title &&
                    oldItem.publisher == newItem.publisher &&
                    oldItem.description == newItem.description
        }
    }


}