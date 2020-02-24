package com.newsboard.ui.sourcearticles

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsboard.R
import com.newsboard.adapters.ArticlesAdapter
import com.newsboard.data.models.articles.Article
import com.newsboard.databinding.FragmentSourceArticleListBinding
import com.newsboard.utils.base.BaseFragment
import com.newsboard.utils.base.ResponseState
import kotlinx.android.synthetic.main.layout_empty_error_state.*
import kotlinx.android.synthetic.main.layout_list_with_states.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class SourceArticleListFragment : BaseFragment<FragmentSourceArticleListBinding>(),
    ArticlesAdapter.ArticleActionHandler {

    override val layoutId: Int
        get() = R.layout.fragment_source_article_list

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var sourceArticleListViewModel: SourceArticleListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            sourceArticleListViewModel.getSourceArticles(arrayOf(it.getString("source_id")))
            tv_toolbar.text = it.getString("source_name")
        }
    }

    override fun init() {
        articlesAdapter = ArticlesAdapter(this)
    }

    override fun initLiveData() {
        sourceArticleListViewModel =
            ViewModelProvider(this).get(SourceArticleListViewModel::class.java)

        sourceArticleListViewModel.sourceArticlesLiveData.observe(this, Observer {
            when (it) {
                is ResponseState.Success -> {
                    dataBinding.pbLoader.hide()
                    vs_state.visibility = View.VISIBLE
                    vs_state.displayedChild = 0
                    articlesAdapter.submitList(it.output)
                }

                else -> {
                    if (it is ResponseState.Loading) {
                        vs_state.visibility = View.INVISIBLE
                        dataBinding.pbLoader.show()
                    } else {
                        dataBinding.pbLoader.hide()
                        vs_state.visibility = View.VISIBLE
                        vs_state.displayedChild = 1
                        setEmptyErrorStates(
                            it,
                            iv_error,
                            iv_error_title,
                            iv_error_desc
                        )
                    }
                }
            }
        })
    }

    override fun setupViews() {
        (activity as AppCompatActivity).setSupportActionBar(tl_home)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tbl_home.visibility = View.GONE
        tv_toolbar.gravity = Gravity.CENTER_VERTICAL

        rv_list.adapter = articlesAdapter
        rv_list.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun setListeners() {
        tl_home?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onViewArticle(selectedArticle: Article) {
        activity?.let {
            CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                .addDefaultShareMenuItem()
                .setShowTitle(true)
                .build()
                .launchUrl(it, Uri.parse(selectedArticle.url))
        }
    }

    override fun onBookMarkArticle(
        selectedArticle: Article,
        adapterPosition: Int
    ) {
        selectedArticle.bookMarked = !selectedArticle.bookMarked
    }

    override fun onShareArticle(selectedArticle: Article) {
        val shareIntent = ShareCompat.IntentBuilder.from(activity!!)
            .setType("text/plain")
            .setText("Checkout this news -> ${selectedArticle.url}")
            .intent

        activity?.let {
            if (shareIntent.resolveActivity(it.packageManager) != null) {
                startActivity(shareIntent)
            }
        }
    }
}