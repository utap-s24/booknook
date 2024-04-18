package com.example.booknook.ui.home

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.booknook.MainViewModel
import com.example.booknook.R
import com.example.booknook.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import retrofit2.HttpException

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun initAdapter(binding: FragmentHomeBinding) {
        val booksGridAdapter = BookGridAdapter(viewModel, {
            AddToBookBoardFragment.newInstance(it).show(parentFragmentManager, "AddToBookBoardFragment")
        }, {
            Toast.makeText(context, "Removed $it", Toast.LENGTH_SHORT).show()
        }, {
            BookInfoPopup.newInstance(it).show(parentFragmentManager, "BookInfoPopup")
        })

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
        // disabling refresh because nyt api content updates weekly so there is nothing
        // new to refresh and see
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
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}