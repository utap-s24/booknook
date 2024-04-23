package com.example.booknook.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.booknook.R
import com.example.booknook.databinding.FragmentSearchUsersBinding

class SearchProfileFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchUsersBinding? = null

    private fun initAdapter(binding: FragmentSearchUsersBinding) {
        val adapter = SearchUserRowAdapter(viewModel, viewModel.getAllUsers()) {
            val action = SearchProfileFragmentDirections.navigationSearchToPreview(it)
            findNavController().navigate(action)
        }
        binding.searchedUsers.adapter = adapter
        binding.searchedUsers.layoutManager = LinearLayoutManager(activity)
        val itemDecor = DividerItemDecoration(binding.searchedUsers.context, LinearLayoutManager.VERTICAL)
        binding.searchedUsers.addItemDecoration(itemDecor)

        viewModel.observeUserMatches().observe(viewLifecycleOwner) {
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

        binding.profileSearch.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // AI CONTRIBUTION, showing keyboard
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.profileSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val allUsers = viewModel.getAllUsers()
                    var userMatches = emptyList<User>().toMutableList()
                    for (user in allUsers) {
                        if (user.username.contains(query)) {
                            userMatches.add(user)
                        }
                    }
                    viewModel.updateUserMatches(userMatches)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty())
                    viewModel.updateUserMatches(viewModel.getAllUsers())
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}