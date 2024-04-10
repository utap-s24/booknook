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
import com.example.booknook.databinding.PopupDeleteBinding

class DeleteBoardPopup : DialogFragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: PopupDeleteBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance(bookBoard: BookBoard): DeleteBoardPopup {
            val args = Bundle().apply {
                putSerializable("bookBoardKey", bookBoard)
            }
            return DeleteBoardPopup ().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // AI GENERATED LINE OF CODE TO GET TRANSPARENT BACKGROUND FOR ROUNDED CORNERS
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val book = arguments?.getSerializable("bookBoardKey", BookBoard::class.java)

        binding.bookBoardName.text = book?.bookBoardTitle
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        binding.buttonDelete.setOnClickListener {
            // XXX call view model delete
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
