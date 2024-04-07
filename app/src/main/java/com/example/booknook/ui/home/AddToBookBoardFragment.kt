package com.example.booknook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booknook.MainViewModel
import com.example.booknook.api.Book
import com.example.booknook.databinding.FragmentAddToBookboardBinding
import com.example.booknook.glide.Glide
import com.example.booknook.ui.boards.SavedBook
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddToBookBoardFragment : BottomSheetDialogFragment()  {
    private val viewModel: MainViewModel by activityViewModels()
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
    private fun initAdapter(binding: FragmentAddToBookboardBinding, book : Book) {
        val bookBoardsListAdapter = BookBoardsCheckboxAdapter(viewModel) {
//           it.booksInBoard.add(book)
            // CALL VIEWMODEL ADD_TO_BOOKBOARD_DATABASE AND HAVE IT RETURN NEW ID
            // PASS ID TO IT.BOOKSINBOARD.ADD(SAVEDBOOK(ID, ...)
            it.booksInBoard.add(SavedBook("tempId", book.title, listOf(book.author), book.imageUrl, book.isbn10, book.isbn13))
        }
        viewModel.observeBookBoardsList().observe(viewLifecycleOwner) {
            bookBoardsListAdapter.submitList(it)
        }
        binding.recyclerViewChecklist.adapter = bookBoardsListAdapter
        binding.recyclerViewChecklist.layoutManager = LinearLayoutManager(activity)
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


        val book = arguments?.getSerializable("bookKey") as Book?

        if (book != null) {
            initAdapter(binding, book)
        }

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
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}