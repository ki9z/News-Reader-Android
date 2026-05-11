package com.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.R
import com.databinding.FragmentDownloadsBinding
import com.ui.adapter.NewsAdapter
import com.ui.model.NewsUiModel
import com.util.UiState
import com.util.gone
import com.util.visible
import com.viewmodel.profile.DownloadsViewModel
import com.viewmodel.profile.DownloadsViewModelFactory
import kotlinx.coroutines.launch

class DownloadsFragment : Fragment(R.layout.fragment_downloads) {

    private var _binding: FragmentDownloadsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DownloadsViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        DownloadsViewModelFactory(app.profileRepository)
    }

    private lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDownloadsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setupControls()
        setupRecyclerView()
        observeUi()
        observeHeader()
    }

    private fun setupControls() {
        binding.etSearch.doAfterTextChanged { editable ->
            viewModel.setQuery(editable?.toString().orEmpty())
        }

        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipDownloaded -> viewModel.setStatusFilter(DownloadsViewModel.StatusFilter.DOWNLOADED)
                R.id.chipFailed -> viewModel.setStatusFilter(DownloadsViewModel.StatusFilter.FAILED)
                R.id.chipExpired -> viewModel.setStatusFilter(DownloadsViewModel.StatusFilter.EXPIRED)
                else -> viewModel.setStatusFilter(DownloadsViewModel.StatusFilter.ALL)
            }
        }

        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllDownloads()
            Toast.makeText(requireContext(), R.string.profile_clear_all, Toast.LENGTH_SHORT).show()
        }

        binding.switchWifiOnly.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                if (isChecked) R.string.profile_pref_enabled else R.string.profile_pref_disabled,
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switchAutoDownloadBookmarks.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                if (isChecked) R.string.profile_pref_enabled else R.string.profile_pref_disabled,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(::openDetail)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList.getOrNull(viewHolder.adapterPosition) ?: return
                viewModel.removeDownload(item.articleUrl.orEmpty())
                Toast.makeText(requireContext(), R.string.profile_remove_from_history, Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun observeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.gone()
                    binding.recyclerView.gone()
                    binding.layoutEmpty.root.gone()

                    when (state) {
                        is UiState.Loading -> binding.progressBar.visible()

                        is UiState.Success -> {
                            binding.recyclerView.visible()
                            adapter.submitList(state.data)
                        }

                        is UiState.Empty -> binding.layoutEmpty.root.visible()

                        is UiState.Error,
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun observeHeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.headerState.collect { header ->
                    binding.tvStats.text = getString(
                        R.string.profile_download_stats,
                        header.totalDownloaded,
                        header.storageUsedText
                    )
                }
            }
        }
    }

    private fun openDetail(item: NewsUiModel) {
        val articleUrl = item.articleUrl.orEmpty()
        if (articleUrl.isBlank()) {
            Toast.makeText(requireContext(), R.string.profile_download_invalid, Toast.LENGTH_SHORT).show()
            return
        }

        findNavController().navigate(
            R.id.action_downloadsFragment_to_detailFragment,
            bundleOf(
                "title" to item.title,
                "description" to item.description.orEmpty(),
                "content" to item.content.orEmpty(),
                "imageUrl" to item.imageUrl.orEmpty(),
                "articleUrl" to articleUrl,
                "sourceName" to item.sourceName,
                "author" to item.author.orEmpty(),
                "publishedAt" to item.publishedAt.orEmpty(),
                "fromScreen" to "downloads"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}