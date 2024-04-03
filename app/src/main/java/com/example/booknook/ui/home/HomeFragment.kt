package com.example.booknook.ui.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.booknook.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initAdapter(binding: FragmentHomeBinding) {
        val booksGridAdapter = BookGridAdapter(viewModel) {
            AddToBookBoardFragment.newInstance(it).show(parentFragmentManager, "add to bookboard popup")
        }

        viewModel.observeNetBooks().observe(viewLifecycleOwner) {
            binding.popularText.visibility = View.VISIBLE
            booksGridAdapter.submitList(it)
        }

        viewModel.observeNetSearchBooks().observe(viewLifecycleOwner) {
            binding.popularText.visibility = View.INVISIBLE
            booksGridAdapter.submitList(viewModel.transformGoogleBookIntoBook(it))
        }

        binding.recyclerViewGrid.adapter = booksGridAdapter
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewGrid.layoutManager = layoutManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(binding)
        initSwipeLayout(binding.swipeRefreshLayout)
        binding.SearchBar.setOnClickListener {
            binding.SearchBar.requestFocusFromTouch()
        }

        binding.SearchBar.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // AI CONTRIBUTION, showing keyboard
                val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        binding.SearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)
                    viewModel.searchGoogleBooks(query)
                binding.popularText.visibility = View.GONE
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Text has changed, newText is the current content of SearchView
                if (newText.isNullOrEmpty())
                    viewModel.searchGoogleBooks("")
                return false
            }
        })
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        // XXX Write me
        swipe.isRefreshing = false
        viewModel.observeFetchDone().observe(viewLifecycleOwner) {
            swipe.isRefreshing = it == false
        }
        // not sure if this is right
        swipe.setOnRefreshListener {
            viewModel.netRefresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}