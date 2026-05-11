package com.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import android.content.Intent
import com.example.app_doc_bao.MainActivity
import com.R
import com.databinding.FragmentProfileBinding
import com.util.loadImage
import com.viewmodel.profile.ProfileViewModel
import com.viewmodel.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        ProfileViewModelFactory(app.userSettingsRepository)
    }

    private var isBindingFromState = false

    private val languageItems = listOf(
        "en" to R.string.profile_language_english,
        "vi" to R.string.profile_language_vietnamese
    )

    private val textSizeItems = listOf(
        "S" to R.string.profile_text_size_small,
        "M" to R.string.profile_text_size_medium,
        "L" to R.string.profile_text_size_large
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupActions()
        observeSettings()
    }

    private fun setupActions() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        }
        binding.btnEditProfile.setOnClickListener { openEditProfileDialog() }

        binding.rowSavedArticles.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_bookmarkFragment)
        }
        binding.rowReadingHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_readingHistoryFragment)
        }
        binding.rowFollowingTopics.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followingTopicsFragment)
        }
        binding.rowDownloads.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_downloadsFragment)
        }

        binding.rowAdmin.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.rowLanguage.setOnClickListener {
            showSingleChoiceDialog(
                titleRes = R.string.profile_language,
                options = languageItems,
                selectedCode = viewModel.settings.value.languageCode
            ) { selectedCode ->
                viewModel.setLanguage(selectedCode)
                applyLanguage(selectedCode)
                toast(getString(R.string.profile_language_updated))
            }
        }

        binding.rowTextSize.setOnClickListener {
            showSingleChoiceDialog(
                titleRes = R.string.profile_text_size,
                options = textSizeItems,
                selectedCode = viewModel.settings.value.textSize
            ) { selectedSize ->
                viewModel.setTextSize(selectedSize)
                toast(getString(R.string.profile_text_size_updated))
            }
        }

        binding.rowAccountSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        }
        binding.rowPrivacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_privacyPolicyFragment)
        }
        binding.rowHelpSupport.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_helpSupportFragment)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            toast(getString(R.string.profile_logout_success))
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingFromState) return@setOnCheckedChangeListener
            viewModel.setDarkModeEnabled(isChecked)
            applyDarkMode(isChecked)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingFromState) return@setOnCheckedChangeListener
            viewModel.setNotificationsEnabled(isChecked)
        }

        binding.switchDataSaver.setOnCheckedChangeListener { _, isChecked ->
            if (isBindingFromState) return@setOnCheckedChangeListener
            viewModel.setDataSaverEnabled(isChecked)
        }
    }

    private fun openEditProfileDialog() {
        val current = viewModel.settings.value
        showEditProfileDialog(current) { input ->
            viewModel.updateProfile(
                displayName = input.displayName,
                email = input.email,
                avatarUrl = input.avatarUrl,
                occupation = input.occupation,
                location = input.location,
                birthday = input.birthday,
                bio = input.bio,
                interests = input.interests
            )
            toast(getString(R.string.profile_saved))
        }
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    isBindingFromState = true
                    binding.tvDisplayName.text = settings.displayName
                    binding.tvEmail.text = settings.email
                    binding.tvLanguageValue.text = languageLabel(settings.languageCode)
                    binding.tvTextSizeValue.text = textSizeLabel(settings.textSize)
                    binding.switchDarkMode.isChecked = settings.darkModeEnabled
                    binding.switchNotifications.isChecked = settings.notificationsEnabled
                    binding.switchDataSaver.isChecked = settings.dataSaverEnabled
                    renderAvatar(settings.avatarUrl)
                    isBindingFromState = false
                }
            }
        }
    }

    private fun renderAvatar(url: String) {
        if (url.isBlank()) {
            binding.ivAvatar.setImageResource(android.R.drawable.ic_menu_myplaces)
            return
        }
        binding.ivAvatar.loadImage(url)
    }

    private fun languageLabel(code: String): String {
        return when (code) {
            "vi" -> getString(R.string.profile_language_vietnamese)
            else -> getString(R.string.profile_language_english)
        }
    }

    private fun textSizeLabel(code: String): String {
        return when (code) {
            "S" -> getString(R.string.profile_text_size_small)
            "L" -> getString(R.string.profile_text_size_large)
            else -> getString(R.string.profile_text_size_medium)
        }
    }

    private fun applyLanguage(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    private fun applyDarkMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

