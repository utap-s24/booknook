package com.example.booknook.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.booknook.api.Book
import com.example.booknook.databinding.BookInfoItemBinding
import com.example.booknook.glide.Glide

class BookInfoPopup: DialogFragment()  {
    private var _binding: BookInfoItemBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance(book: Book): BookInfoPopup {
            val args = Bundle().apply {
                putSerializable("bookKey", book)
            }
            return BookInfoPopup ().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BookInfoItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AI GENERATED LINE OF CODE TO GET TRANSPARENT BACKGROUND FOR ROUNDED CORNERS
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val book = arguments?.getSerializable("bookKey", Book::class.java)

        if (book != null) {
            book.imageUrl.let { Glide.glideFetch(it, binding.bookCover) }
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author
            binding.description.text = book.description

            if (book.genres?.isNotEmpty() == true) {
                binding.genre1.text = book.genres[0]
                if (book.genres.size >= 2)
                    binding.genre2.text = book.genres[1]
                else
                    binding.genre2.visibility = View.GONE
                if (book.genres.size >= 3)
                    binding.genre3.text = book.genres[2]
                else
                    binding.genre3.visibility = View.GONE
            } else {
                binding.genre1.visibility = View.GONE
                binding.genre2.visibility = View.GONE
                binding.genre3.visibility = View.GONE
            }
            if (book.nytRank == 0) {
                if (!book.subtitle.isNullOrEmpty())
                    binding.NewYorkTimesBestsellerTag.text = book.subtitle
                else
                    binding.NewYorkTimesBestsellerTag.visibility = View.GONE
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
