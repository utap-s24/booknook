package com.example.booknook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.booknook.R
import com.example.booknook.databinding.FragmentHomeBinding
import com.example.booknook.ui.profile.AuthInit
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {

    }

    private fun initAdapter(binding: FragmentHomeBinding) {
        val booksGridAdapter = BookGridAdapter(viewModel) {
            AddToBookBoardFragment.newInstance(it).show(parentFragmentManager, "add to bookboard popup")
        }

        viewModel.observeNetBooks().observe(viewLifecycleOwner) {
            booksGridAdapter.submitList(it)
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

        AuthInit(viewModel, signInLauncher)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter(binding)
        initSwipeLayout(binding.swipeRefreshLayout)
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