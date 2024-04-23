package com.example.booknook.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booknook.MainViewModel
import com.example.booknook.databinding.FragmentSearchUsersBinding

class FriendsListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchUsersBinding? = null

    private fun initAdapter(binding: FragmentSearchUsersBinding) {
        val adapter = SearchUserRowAdapter(viewModel, viewModel.getAllUsers()) {
            val action = FriendsListFragmentDirections.navigationFriendsToPreview(it)
            findNavController().navigate(action)
        }
        binding.searchedUsers.adapter = adapter
        binding.searchedUsers.layoutManager = LinearLayoutManager(activity)
        val itemDecor = DividerItemDecoration(binding.searchedUsers.context, LinearLayoutManager.VERTICAL)
        binding.searchedUsers.addItemDecoration(itemDecor)

        viewModel.observeFriendsList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUsersBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(binding)

        binding.profileSearch.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}