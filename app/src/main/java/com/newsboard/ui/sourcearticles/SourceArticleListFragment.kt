package com.newsboard.ui.sourcearticles

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.material.snackbar.Snackbar
import com.newsboard.R
import com.newsboard.adapters.ArticlesAdapter
import com.newsboard.data.models.articles.Article
import com.newsboard.databinding.FragmentSourceArticleListBinding
import com.newsboard.utils.base.BaseFragment
import com.newsboard.utils.base.ResponseState
import com.newsboard.utils.impls.IntrinsicDrawableClickListenerImpl
import com.newsboard.utils.impls.TextWatchImpl

/**
 * Fragment to show the list of articles on basis of either
 * selected sources or queried via search.
 */
class SourceArticleListFragment : BaseFragment<FragmentSourceArticleListBinding>(),
    ArticlesAdapter.ArticleActionHandler {

    override val layoutId: Int
        get() = R.layout.fragment_source_article_list

    /**
     * Flag to indicate fragment list is used by search queries.
     */
    private var isSearchNeeded = false

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var sourceArticleListViewModel: SourceArticleListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            sourceArticleListViewModel.getSourceArticles(arrayOf(it.getString(getString(R.string.source_id))))

            dataBinding.toolbar.tvToolbar.text =
                if (it.containsKey(getString(R.string.source_name)))
                    it.getString(getString(R.string.source_name))
                else {
                    isSearchNeeded = true
                    dataBinding.toolbar.etSearch.visibility = View.VISIBLE
                    getString(R.string.search)
                }
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
                    dataBinding.vsState.vsState.visibility = View.VISIBLE
                    dataBinding.vsState.vsState.displayedChild = 0
                    articlesAdapter.submitList(it.output)
                }

                else -> {
                    if (it is ResponseState.Loading) {
                        dataBinding.vsState.vsState.visibility = View.INVISIBLE
                        dataBinding.pbLoader.show()
                    } else {
                        dataBinding.pbLoader.hide()
                        dataBinding.vsState.vsState.visibility = View.VISIBLE
                        dataBinding.vsState.vsState.displayedChild = 1
                        setEmptyErrorStates(
                            it,
                            dataBinding.vsState.errorState.ivError,
                            dataBinding.vsState.errorState.ivErrorTitle,
                            dataBinding.vsState.errorState.ivErrorDesc
                        )
                    }
                }
            }
        })
    }

    override fun setupViews() {
        (activity as AppCompatActivity).setSupportActionBar(dataBinding.toolbar.tlHome)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataBinding.toolbar.tblHome.visibility = View.GONE
        dataBinding.toolbar.tvToolbar.gravity = Gravity.CENTER_VERTICAL

        dataBinding.vsState.rvList.adapter = articlesAdapter
        dataBinding.vsState.rvList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    // Fields for DEBOUNCE logic for search queries.
    private val mainHandler = Handler(Looper.getMainLooper())
    private var articlesApiHitRunnable: Runnable = Runnable {
        if (dataBinding.toolbar.etSearch.text.isNotBlank())
            sourceArticleListViewModel.getSourceArticles(emptyArray(), dataBinding.toolbar.etSearch.text.toString())
    }

    override fun setListeners() {
        dataBinding.toolbar.tlHome.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        dataBinding.toolbar.etSearch.addTextChangedListener(object : TextWatchImpl() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    dataBinding.toolbar.etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_close,
                        0
                    )

                    articlesApiHitRunnable.run {
                        mainHandler.removeCallbacks(this)
                        mainHandler.postDelayed(this, 500)
                    }

                } else {
                    dataBinding.toolbar.etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_search,
                        0
                    )
                }
            }
        })

        dataBinding.toolbar.etSearch.setOnTouchListener(object : IntrinsicDrawableClickListenerImpl() {
            override fun onDrawableEndClicked() {
                dataBinding.toolbar.etSearch.setText("")
            }
        })
    }

    override fun onViewArticle(selectedArticle: Article) {
        activity?.let {
            CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
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
        articlesAdapter.notifyItemChanged(adapterPosition, selectedArticle)

        // todo remove pun with functionality
        Snackbar.make(
            dataBinding.root,
            getString(R.string.developer_msg),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onShareArticle(selectedArticle: Article) {
        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity())
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