package com.newsboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.newsboard.data.models.articles.Article
import com.newsboard.databinding.ListArticleBinding

class ArticlesAdapter(private val articlesActionHandler: ArticleActionHandler) :
    PagedListAdapter<Article, ArticlesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, articlesActionHandler) }
    }

    class ViewHolder(private val binding: ListArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            article: Article,
            articlesActionHandler: ArticleActionHandler
        ) {
            binding.currItem = article
            binding.actionHandler = articlesActionHandler

            binding.ivBookmark.isActivated = article.bookMarked

            binding.root.setOnClickListener { articlesActionHandler.onViewArticle(article) }

            binding.ivBookmark.setOnClickListener {
                articlesActionHandler.onBookMarkArticle(article, adapterPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.title == newItem.title
        }
    }

    interface ArticleActionHandler {
        fun onViewArticle(selectedArticle: Article)
        fun onBookMarkArticle(
            selectedArticle: Article,
            adapterPosition: Int
        )
        fun onShareArticle(selectedArticle: Article)
    }
}