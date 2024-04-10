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
import com.example.booknook.databinding.PopupCreateBookboardBinding

class CreateBookBoardPopup: DialogFragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: PopupCreateBookboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupCreateBookboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AI GENERATED LINE OF CODE TO GET TRANSPARENT BACKGROUND FOR ROUNDED CORNERS
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.buttonCreate.setOnClickListener {
            val bookBoardName = binding.nameEditText.text.toString()
            if (bookBoardName.isNotEmpty()) {
                viewModel.createBookBoard(bookBoardName, true)
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}