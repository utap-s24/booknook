package com.example.booknook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.booknook.R
import com.example.booknook.api.Book
import com.example.booknook.databinding.FragmentAddToBookboardBinding
import com.example.booknook.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddToBookBoardFragment : BottomSheetDialogFragment()  {
    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentAddToBookboardBinding? = null

    private val binding get() = _binding!!

    companion object {
        // CHATGPT CONTRIBUTION
        fun newInstance(book: Book): AddToBookBoardFragment {
            val args = Bundle().apply {
                putSerializable("bookKey", book)
            }
            return AddToBookBoardFragment().apply {
                arguments = args
            }
        }
    }
    private fun initAdapter(binding: FragmentAddToBookboardBinding) {
        // XXX Recycler view for BookBoards
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddToBookboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter(binding)
        val book = arguments?.getSerializable("bookKey") as Book?

        if (book != null) {
            val layoutParams = binding.bookCover.layoutParams
            layoutParams.width = book.bookImageWidth
            layoutParams.height = book.bookImageHeight
            binding.bookCover.layoutParams = layoutParams
            Glide.glideFetch(book.imageUrl, binding.bookCover)
        }
        binding.titleText.text = book?.title
        binding.AuthorText.text = book?.author

        binding.doneButton.setOnClickListener {
            // XXX COLLECT CHECKED BOOK BOARDS FROM RECYCLER VIEW & ADD BOOK
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}