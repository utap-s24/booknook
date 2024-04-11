package com.example.booknook.ui.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.booknook.MainViewModel
import com.example.booknook.R
import com.example.booknook.databinding.OneBoardFragmentBinding
import com.example.booknook.ui.boards.OneBoardFragmentArgs

class OneBoardFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val args: OneBoardFragmentArgs by navArgs()
    private var _binding: OneBoardFragmentBinding? = null

    private val binding get() = _binding!!

    private fun initAdapter(binding: OneBoardFragmentBinding) {
        val bookBoardAdapter = BookBoardAdapter(viewModel) {
            DeleteBookInBoardPopup.newInstance(it, args.bookBoard).show(parentFragmentManager, "DeleteBookInBoardPopup")
        }
        bookBoardAdapter.submitList(args.bookBoard.booksInBoard)
        binding.recyclerViewBoard.adapter = bookBoardAdapter
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewBoard.layoutManager = layoutManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OneBoardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter(binding)
        binding.bookboardName.text = args.bookBoard.bookBoardTitle
        val booksCountText = "${args.bookBoard.booksInBoard.size} Books"
        binding.booksCount.text = booksCountText

        if (args.bookBoard.isPublic) {
            binding.publicButton.setImageResource(R.drawable.baseline_lock_open_24)
        } else {
            binding.publicButton.setImageResource(R.drawable.baseline_lock_24)
        }
        binding.publicButton.setOnClickListener {
            if (args.bookBoard.isPublic) {
                binding.publicButton.setImageResource(R.drawable.baseline_lock_24)
                args.bookBoard.isPublic = false
            } else {
                args.bookBoard.isPublic = true
                binding.publicButton.setImageResource(R.drawable.baseline_lock_open_24)
            }
            viewModel.updateBookBoardPublicStatus(args.bookBoard)
        }
        binding.deleteIconButton.setOnClickListener {
            val deletePopup = DeleteBoardPopup.newInstance(args.bookBoard)
            deletePopup.show(parentFragmentManager, "DeleteBoardPopup")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}