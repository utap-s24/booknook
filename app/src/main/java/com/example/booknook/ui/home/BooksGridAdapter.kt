package com.example.booknook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.MainViewModel
import com.example.booknook.R
import com.example.booknook.api.Book
import com.example.booknook.api.GoogleBook
import com.example.booknook.databinding.BookItemBinding
import com.example.booknook.glide.Glide
import com.google.android.material.snackbar.Snackbar

class BookGridAdapter(private val viewModel: MainViewModel,
                      private val navigateToAddToBookBoardPopup: (Book) -> Unit,
                      private val displaySnackbar: (String) -> Unit,
                      private val navigateToBookInfoPopup: (Book) -> Unit)
    : ListAdapter<Book, BookGridAdapter.VH>(BookDiff()) {

    inner class VH(private val bookItemBinding: BookItemBinding) : RecyclerView.ViewHolder(bookItemBinding.root) {
        fun bind(bookItem: Book) {
            // setting book height and width for photo
            if (bookItem.bookImageHeight > 0 && bookItem.bookImageWidth > 0) {
                val layoutParams = bookItemBinding.bookCover.layoutParams
                layoutParams.width = bookItem.bookImageWidth
                layoutParams.height = bookItem.bookImageHeight
                bookItemBinding.bookCover.layoutParams = layoutParams
            }
            // setting photo using Glide
            Glide.glideFetch(bookItem.imageUrl, bookItemBinding.bookCover)
            println(bookItem.imageUrl)

            // setting bookmarked icon functionality
            if (viewModel.isSaved(bookItem.isbn10, bookItem.isbn13))
                bookItemBinding.bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24)
            else
                bookItemBinding.bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_border_24)

            bookItemBinding.bookmarkIcon.setOnClickListener {
                if (!viewModel.isSaved(bookItem.isbn10, bookItem.isbn13)) {
                    bookItemBinding.bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24)
                    viewModel.addBookToBookmarkedList(bookItem)
                    navigateToAddToBookBoardPopup(bookItem)
                }
                else {
                    bookItemBinding.bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_border_24)
                    viewModel.removeBookFromBookmarkedList(bookItem)
                    displaySnackbar(bookItem.title)
                }
            }
            bookItemBinding.bookCover.setOnClickListener {
                navigateToBookInfoPopup(bookItem)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
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