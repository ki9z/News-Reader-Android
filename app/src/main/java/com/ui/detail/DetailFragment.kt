package com.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.R
import com.databinding.FragmentDetailBinding
import com.data.model.Article
import com.data.model.Source
import com.util.DateUtils
import com.util.loadImage
import com.viewmodel.detail.DetailViewModel
import com.viewmodel.detail.DetailViewModelFactory
import kotlinx.coroutines.launch

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        val app = requireActivity().application as com.NewsApp
        DetailViewModelFactory(app.repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)

        val args = requireArguments()
        val title = args.getString("title", "")
        val description = args.getString("description", "")
        val content = args.getString("content", "")
        val imageUrl = args.getString("imageUrl", "")
        val articleUrl = args.getString("articleUrl", "")
        val sourceName = args.getString("sourceName", "")
        val author = args.getString("author", "")
        val publishedAt = args.getString("publishedAt", "")

        setupToolbar()
        bindData(title, description, content, imageUrl, sourceName, author, publishedAt)
        setupActions(articleUrl, Article(
            source = Source(null, sourceName),
            author = author.takeIf { it.isNotBlank() },
            title = title,
            description = description.takeIf { it.isNotBlank() },
            url = articleUrl.takeIf { it.isNotBlank() },
            urlToImage = imageUrl.takeIf { it.isNotBlank() },
            publishedAt = publishedAt.takeIf { it.isNotBlank() },
            content = content.takeIf { it.isNotBlank() }
        ))
        observeBookmark()

        if (articleUrl.isNotBlank()) {
            viewModel.checkBookmarkStatus(articleUrl)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindData(
        title: String,
        description: String,
        content: String,
        imageUrl: String,
        sourceName: String,
        author: String,
        publishedAt: String
    ) {
        binding.tvTitle.text = title
        binding.tvSource.text = sourceName
        binding.tvAuthor.text = if (author.isNotBlank()) "By $author" else ""
        binding.tvPublishedAt.text = DateUtils.formatPublishedAt(publishedAt)
        binding.tvDescription.text = description
        binding.tvContent.text = if (content.isNotBlank()) content else getString(R.string.content_not_available)

        if (imageUrl.isNotBlank()) {
            binding.ivCover.loadImage(imageUrl)
        }
    }

    private fun setupActions(articleUrl: String, article: Article) {
        binding.btnOpenArticle.setOnClickListener {
            if (articleUrl.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
                startActivity(intent)
            }
        }

        binding.fabBookmark.setOnClickListener {
            viewModel.toggleBookmark(articleUrl, article)
        }
    }

    private fun observeBookmark() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isBookmarked.collect { isBookmarked ->
                    binding.fabBookmark.setImageResource(
                        if (isBookmarked) android.R.drawable.btn_star_big_on
                        else android.R.drawable.btn_star_big_off
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
