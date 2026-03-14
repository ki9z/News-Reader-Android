package com.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.databinding.ItemNewsBinding
import com.ui.model.NewsUiModel
import com.util.DateUtils
import com.util.loadImage

class NewsAdapter(
    private val onItemClick: (NewsUiModel) -> Unit
) : ListAdapter<NewsUiModel, NewsAdapter.NewsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsUiModel) {
            binding.tvTitle.text = item.title
            binding.tvSource.text = item.sourceName
            binding.tvPublishedAt.text = DateUtils.formatPublishedAt(item.publishedAt)
            binding.ivThumbnail.loadImage(item.imageUrl)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NewsUiModel>() {
        override fun areItemsTheSame(oldItem: NewsUiModel, newItem: NewsUiModel): Boolean {
            return oldItem.articleUrl == newItem.articleUrl
        }

        override fun areContentsTheSame(oldItem: NewsUiModel, newItem: NewsUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
