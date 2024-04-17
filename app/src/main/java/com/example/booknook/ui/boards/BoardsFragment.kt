package com.example.booknook.ui.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booknook.MainViewModel
import com.example.booknook.databinding.FragmentBoardsBinding

class BoardsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentBoardsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initAdapter(binding: FragmentBoardsBinding) {
        val bookBoardsAdapter = BookBoardRowAdapter(viewModel) {
            findNavController().navigate(BoardsFragmentDirections.navigationBoardsToOneBoard(it))
        }

        viewModel.observeBookBoardsList().observe(viewLifecycleOwner) {
            bookBoardsAdapter.submitList(it)
        }
        binding.recyclerViewBoards.adapter = bookBoardsAdapter
        binding.recyclerViewBoards.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter(binding)
        binding.newBookBoardButton.setOnClickListener {
            val createBookBoardPopup = CreateBookBoardPopup()
            createBookBoardPopup.show(parentFragmentManager, "CreateBookBoardPopup")
        }
//        binding.savedBooksButton.setOnClickListener {
//            findNavController().navigate(BoardsFragmentDirections.navigationBoardsToOneBoard(viewModel.getSavedBooksAsBoard()))
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}