package com.example.booknook.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booknook.MainViewModel
import com.example.booknook.R
import com.example.booknook.databinding.FragmentUserPreviewBinding
import com.example.booknook.ui.boards.BookBoardRowAdapter

class UserPreviewFragment : Fragment(R.layout.fragment_user_preview) {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentUserPreviewBinding? = null
    private val args : UserPreviewFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initAdapter(binding: FragmentUserPreviewBinding) {
        val bookBoardsAdapter = BookBoardRowAdapter(viewModel) {
        }
        viewModel.fetchFriendBookBoards(args.user)
        viewModel.observeFriendsBookBoards().observe(viewLifecycleOwner) { bookBoards ->
            // only submitting public BookBoards
            val publicBookBoards = bookBoards.filter { book -> book.isPublic }
            bookBoardsAdapter.submitList(publicBookBoards)
        }

        binding.recyclerViewBoards.adapter = bookBoardsAdapter
        binding.recyclerViewBoards.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeUserPreview().observe(viewLifecycleOwner) {

        }
        if (args.user.displayName.isEmpty()) {
            binding.username.text = args.user.username
        } else {
            binding.username.text = args.user.displayName
        }
        binding.bio.text = args.user.aboutMe

        initAdapter(binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}