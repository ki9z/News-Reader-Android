package com.ui.home

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
import com.databinding.FragmentHomeBinding
import com.ui.adapter.NewsAdapter
import com.ui.model.NewsUiModel
import com.util.Constants
import com.util.UiState
import com.util.gone
import com.util.visible
import com.viewmodel.home.HomeViewModel
import com.viewmodel.home.HomeViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        HomeViewModelFactory(app.repository)
    }

    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        setupCategoryButtons()
        setupActions()
        observeUi()

        viewModel.loadInitialNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(::navigateToDetail)

        binding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupCategoryButtons() {
        binding.btnGeneral.setOnClickListener { viewModel.selectCategory(Constants.CATEGORY_GENERAL) }
        binding.btnBusiness.setOnClickListener { viewModel.selectCategory(Constants.CATEGORY_BUSINESS) }
        binding.btnSports.setOnClickListener { viewModel.selectCategory(Constants.CATEGORY_SPORTS) }
        binding.btnTechnology.setOnClickListener { viewModel.selectCategory(Constants.CATEGORY_TECHNOLOGY) }
    }

    private fun setupActions() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshNews()
        }

        binding.layoutError.btnRetry.setOnClickListener {
            viewModel.loadInitialNews()
        }
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
        binding.swipeRefreshLayout.isRefreshing = false

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
            R.id.action_homeFragment_to_detailFragment,
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
