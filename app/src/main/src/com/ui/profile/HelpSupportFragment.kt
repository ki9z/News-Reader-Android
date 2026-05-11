package com.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.R
import com.databinding.FragmentHelpSupportBinding

class HelpSupportFragment : Fragment(R.layout.fragment_help_support) {

    private var _binding: FragmentHelpSupportBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHelpSupportBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.tvContent.setOnLongClickListener {
            openSupportMail(getString(R.string.profile_help_email_subject))
            true
        }

        binding.faqAccount.setOnClickListener {
            findNavController().navigate(R.id.accountSettingsFragment)
        }
        binding.faqHistory.setOnClickListener {
            findNavController().navigate(R.id.readingHistoryFragment)
        }
        binding.faqDownloads.setOnClickListener {
            findNavController().navigate(R.id.downloadsFragment)
        }
        binding.faqNotifications.setOnClickListener {
            Toast.makeText(requireContext(), R.string.profile_topic_alerts, Toast.LENGTH_SHORT).show()
        }
        binding.faqPrivacy.setOnClickListener {
            findNavController().navigate(R.id.privacyPolicyFragment)
        }

        binding.btnContactSupport.setOnClickListener {
            openSupportMail(getString(R.string.profile_help_email_subject))
        }
        binding.btnReportBug.setOnClickListener {
            openSupportMail(getString(R.string.profile_help_report_bug))
        }
        binding.btnSendFeedback.setOnClickListener {
            openSupportMail(getString(R.string.profile_help_send_feedback))
        }

        binding.btnClearCache.setOnClickListener {
            Toast.makeText(requireContext(), R.string.profile_help_clear_cache, Toast.LENGTH_SHORT).show()
        }
        binding.btnRefreshRecommendations.setOnClickListener {
            Toast.makeText(requireContext(), R.string.profile_help_refresh_recommendations, Toast.LENGTH_SHORT).show()
        }
        binding.btnResyncAccount.setOnClickListener {
            Toast.makeText(requireContext(), R.string.profile_help_resync_account, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSupportMail(subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@newsreader.app")
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), R.string.profile_help_no_mail_app, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

