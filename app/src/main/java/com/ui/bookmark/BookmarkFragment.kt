package com.ui.bookmark

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.R
import com.databinding.FragmentBookmarkBinding
import com.ui.adapter.NewsAdapter
import com.ui.model.NewsUiModel
import com.util.UiState
import com.util.gone
import com.util.visible
import com.viewmodel.bookmark.BookmarkViewModel
import com.viewmodel.bookmark.BookmarkViewModelFactory
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarkViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        BookmarkViewModelFactory(app.repository)
    }

    private lateinit var adapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookmarkBinding.bind(view)

        setupRecyclerView()
        observeUi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBookmarks()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(::navigateToDetail)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: UiState<List<NewsUiModel>>) {
        binding.progressBar.gone()
        binding.recyclerView.gone()
        binding.layoutEmpty.root.gone()
        binding.layoutError.root.gone()

        when (state) {
            is UiState.Loading -> binding.progressBar.visible()
            is UiState.Success -> {
                binding.recyclerView.visible()
                adapter.submitList(state.data)
            }
            is UiState.Empty -> binding.layoutEmpty.root.visible()
            is UiState.Error -> {
                binding.layoutError.root.visible()
                binding.layoutError.tvErrorMessage.text = state.message
            }
            is UiState.Idle -> Unit
        }
    }

    private fun navigateToDetail(item: NewsUiModel) {
        findNavController().navigate(
            R.id.action_bookmarkFragment_to_detailFragment,
            bundleOf(
                "title" to item.title,
                "description" to item.description,
                "content" to item.content,
                "imageUrl" to item.imageUrl,
                "articleUrl" to item.articleUrl,
                "sourceName" to item.sourceName,
                "author" to item.author,
                "publishedAt" to item.publishedAt
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
