package com.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.NewsApp
import com.R
import com.data.model.Article
import com.data.model.Source
import com.databinding.FragmentDetailBinding
import com.viewmodel.detail.DetailViewModel
import com.viewmodel.detail.DetailViewModelFactory
import kotlinx.coroutines.launch

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        val app = requireActivity().application as NewsApp
        DetailViewModelFactory(
            repository = app.repository,
            profileRepository = app.profileRepository,
            userSettingsRepository = app.userSettingsRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)

        val args = requireArguments()
        val title = args.getString("title").orEmpty()
        val description = args.getString("description").orEmpty()
        val content = args.getString("content").orEmpty()
        val imageUrl = args.getString("imageUrl").orEmpty()
        val articleUrl = args.getString("articleUrl").orEmpty()
        val sourceName = args.getString("sourceName").orEmpty()
        val author = args.getString("author").orEmpty()
        val publishedAt = args.getString("publishedAt").orEmpty()
        val fromScreen = args.getString("fromScreen").orEmpty()

        val article = Article(
            source = Source(id = null, name = sourceName),
            author = author.ifBlank { null },
            title = title,
            description = description.ifBlank { null },
            url = articleUrl,
            urlToImage = imageUrl.ifBlank { null },
            publishedAt = publishedAt.ifBlank { null },
            content = content.ifBlank { null }
        )

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        binding.tvContent.text = content
        binding.tvSource.text = sourceName
        binding.tvAuthor.text = author
        binding.tvPublishedAt.text = publishedAt

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnOpenArticle.setOnClickListener {
            if (articleUrl.isNotBlank()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl)))
            }
        }

        binding.fabBookmark.setOnClickListener {
            viewModel.toggleBookmark(articleUrl, article)
        }

        binding.webViewArticle.webViewClient = WebViewClient()

        observeBookmarkState()

        if (articleUrl.isNotBlank()) {
            viewModel.checkBookmarkStatus(articleUrl)
            viewModel.recordReading(article, fromScreen)
        }
    }

    private fun observeBookmarkState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isBookmarked.collect { isBookmarked ->
                    binding.fabBookmark.setImageResource(
                        if (isBookmarked) android.R.drawable.btn_star_big_on
                        else android.R.drawable.btn_star_big_off
                    )
                    binding.fabBookmark.imageTintList = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isBookmarked) R.color.news_chip_selected_bg else R.color.news_secondary_text
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
