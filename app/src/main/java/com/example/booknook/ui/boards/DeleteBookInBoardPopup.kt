package com.example.booknook.ui.boards

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.booknook.MainViewModel
import com.example.booknook.databinding.PopupDeleteBookBinding

class DeleteBookInBoardPopup : DialogFragment()  {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: PopupDeleteBookBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance(book: SavedBook, bookBoard: BookBoard): DeleteBookInBoardPopup {
            val args = Bundle().apply {
                putSerializable("bookKey", book)
                putSerializable("bookBoardKey", bookBoard)
            }
            return DeleteBookInBoardPopup().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupDeleteBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // AI GENERATED LINE OF CODE TO GET TRANSPARENT BACKGROUND FOR ROUNDED CORNERS
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val book = arguments?.getSerializable("bookKey", SavedBook::class.java)
        val bookBoard =  arguments?.getSerializable("bookBoardKey", BookBoard::class.java)

        binding.bookName.text = book?.title
        print("authors: ${book?.authors?.joinToString(",").toString()} ")
        binding.bookAuthor.text = book?.authors?.joinToString(", ").toString()
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        binding.buttonDelete.setOnClickListener {
            if (bookBoard != null && book != null) {
                viewModel.deleteBookFromBookBoard(bookBoard, book)
            }
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}