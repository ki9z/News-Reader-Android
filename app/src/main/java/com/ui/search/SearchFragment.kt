package com.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.databinding.FragmentSearchBinding
import com.ui.adapter.NewsAdapter
import com.ui.model.NewsUiModel
import com.util.UiState
import com.util.gone
import com.util.visible
import com.viewmodel.search.SearchViewModel
import com.viewmodel.search.SearchViewModelFactory
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        SearchViewModelFactory(app.repository)
    }

    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        observeUi()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(::navigateToDetail)
        binding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                if (query.isBlank()) viewModel.clearSearch()
                else viewModel.searchNews(query)
            }
        })
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
                newsAdapter.submitList(state.data)
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
            R.id.action_searchFragment_to_detailFragment,
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
