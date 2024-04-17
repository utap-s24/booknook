package com.example.booknook.ui.boards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.MainViewModel
import com.example.booknook.databinding.BookBoardRowBinding
import com.example.booknook.glide.Glide

class BookBoardRowAdapter(private val viewModel: MainViewModel,
                          private val navigateToBookBoardView : (BookBoard) -> Unit)
    : ListAdapter<BookBoard, BookBoardRowAdapter.VH>(BookBoardDiff()) {

    inner class VH(private val bookBoardRowBinding: BookBoardRowBinding) : RecyclerView.ViewHolder(bookBoardRowBinding.root) {
        fun bind(bookBoard: BookBoard) {
            bookBoardRowBinding.BoardTitle.text = bookBoard.bookBoardTitle
            if (bookBoard.booksInBoard.size > 0) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[0].imageUrl,
                    bookBoardRowBinding.bookPreview1
                )
            }
            else {
                println("${bookBoard.bookBoardTitle} is empty ${bookBoard.booksInBoard}")
//                bookBoardRowBinding.bookPreview1.visibility = View.INVISIBLE
            }
            if (bookBoard.booksInBoard.size >= 2) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[1].imageUrl,
                    bookBoardRowBinding.bookPreview2
                )
            }
//            else {
//                bookBoardRowBinding.bookPreview2.visibility = View.INVISIBLE
//            }

            if (bookBoard.booksInBoard.size >= 3) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[2].imageUrl,
                    bookBoardRowBinding.bookPreview3
                )
            }
//            else {
//                bookBoardRowBinding.bookPreview3.visibility = View.INVISIBLE
//            }
            if (bookBoard.booksInBoard.size >= 4) {
                Glide.glideFetch(
                    bookBoard.booksInBoard[3].imageUrl,
                    bookBoardRowBinding.bookPreview4
                )
            }
//            else {
//                bookBoardRowBinding.bookPreview4.visibility = View.INVISIBLE
//            }

            bookBoardRowBinding.root.setOnClickListener {
                viewModel.setBooksInOneBoardFragment(bookBoard.booksInBoard)
                navigateToBookBoardView(bookBoard)
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
            return oldItem.docId == newItem.docId
        }
        // XXX may need to compare books in board
        override fun areContentsTheSame(oldItem: BookBoard, newItem: BookBoard): Boolean {
            if (oldItem.booksInBoard.size == newItem.booksInBoard.size && oldItem.booksInBoard.size >= 4) {
                return oldItem.bookBoardTitle == newItem.bookBoardTitle &&
                        oldItem.isPublic == newItem.isPublic &&
                        oldItem.booksInBoard[0].docId == newItem.booksInBoard[0].docId &&
                        oldItem.booksInBoard[1].docId == newItem.booksInBoard[1].docId &&
                        oldItem.booksInBoard[2].docId == newItem.booksInBoard[2].docId &&
                        oldItem.booksInBoard[3].docId == newItem.booksInBoard[3].docId
            }
            return oldItem.bookBoardTitle == newItem.bookBoardTitle &&
                    oldItem.isPublic == newItem.isPublic &&
                    oldItem.booksInBoard.isNotEmpty() &&  newItem.booksInBoard.isNotEmpty() &&
                    oldItem.booksInBoard.size == newItem.booksInBoard.size
        }
    }


}