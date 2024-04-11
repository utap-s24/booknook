package com.example.booknook.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booknook.MainViewModel
import com.example.booknook.databinding.FragmentProfileBinding
import com.example.booknook.R
import com.example.booknook.ui.boards.BoardsFragmentDirections
import com.example.booknook.ui.boards.BookBoardRowAdapter

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initAdapter(binding: FragmentProfileBinding) {
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeDisplayName().observe(viewLifecycleOwner) {
            binding.username.text = it
        }

        viewModel.observeBio().observe(viewLifecycleOwner) {
            binding.bio.text = it
        }
        binding.editButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.navigation_edit_profile)
        }

        initAdapter(binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}