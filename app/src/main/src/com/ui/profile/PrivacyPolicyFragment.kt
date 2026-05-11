package com.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.R
import com.databinding.FragmentPrivacyPolicyBinding
import com.viewmodel.profile.ProfileViewModel
import com.viewmodel.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch

class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {

    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        ProfileViewModelFactory(app.userSettingsRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPrivacyPolicyBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val app = requireActivity().application as com.NewsApp

        binding.btnManageActivity.setOnClickListener {
            findNavController().navigate(R.id.readingHistoryFragment)
        }

        binding.btnClearHistory.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                app.profileRepository.clearReadingHistory()
                Toast.makeText(requireContext(), R.string.profile_clear_all, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClearSearch.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                app.profileRepository.clearSearchHistory()
                Toast.makeText(requireContext(), R.string.profile_clear_search, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDisablePersonalization.setOnClickListener {
            profileViewModel.setPersonalizationEnabled(false)
            Toast.makeText(requireContext(), R.string.profile_disable_personalization, Toast.LENGTH_SHORT).show()
        }

        binding.btnRequestDeletion.setOnClickListener {
            profileViewModel.clearLocalAccountData()

            viewLifecycleOwner.lifecycleScope.launch {
                app.profileRepository.clearReadingHistory()
                app.profileRepository.clearSearchHistory()
                app.profileRepository.clearDownloads()
                app.profileRepository.resetFollowingTopics()

                Toast.makeText(
                    requireContext(),
                    R.string.profile_local_data_deleted,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnOpenFullPolicy.setOnClickListener {
            Toast.makeText(requireContext(), R.string.profile_open_full_policy, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}