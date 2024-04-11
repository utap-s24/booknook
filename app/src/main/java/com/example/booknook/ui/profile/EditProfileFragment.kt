package com.example.booknook.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.booknook.MainViewModel
import com.example.booknook.R
import com.example.booknook.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentEditProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeDisplayName().observe(viewLifecycleOwner) {
            binding.username.setText(it)
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }

        binding.saveButton.setOnClickListener {
            viewModel.updateBio(binding.bio.text.toString())
            viewModel.updateDisplayName(binding.username.text.toString())
            findNavController().navigate(R.id.navigation_profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}