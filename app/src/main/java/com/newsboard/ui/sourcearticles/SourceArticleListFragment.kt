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
import kotlinx.android.synthetic.main.layout_empty_error_state.*
import kotlinx.android.synthetic.main.layout_list_with_states.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class SourceArticleListFragment : BaseFragment<FragmentSourceArticleListBinding>(),
    ArticlesAdapter.ArticleActionHandler {

    override val layoutId: Int
        get() = R.layout.fragment_source_article_list

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var sourceArticleListViewModel: SourceArticleListViewModel
    private var isSearchNeeded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            sourceArticleListViewModel.getSourceArticles(arrayOf(it.getString(getString(R.string.source_id))))

            tv_toolbar.text =
                if (it.containsKey(getString(R.string.source_name)))
                    it.getString(getString(R.string.source_name))
                else {
                    isSearchNeeded = true
                    et_search.visibility = View.VISIBLE
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

    // Fields for DEBOUNCE logic for search queries.
    private val mainHandler = Handler(Looper.getMainLooper())
    private var articlesApiHitRunnable: Runnable = Runnable {
        if (!et_search.text.isBlank())
            sourceArticleListViewModel.getSourceArticles(emptyArray(), et_search.text.toString())
    }

    override fun setListeners() {
        tl_home?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        et_search.addTextChangedListener(object : TextWatchImpl() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    et_search.setCompoundDrawablesRelativeWithIntrinsicBounds(
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
                    et_search.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_search,
                        0
                    )
                }
            }
        })

        et_search.setOnTouchListener(object : IntrinsicDrawableClickListenerImpl() {
            override fun onDrawableEndClicked() {
                et_search.setText("")
            }
        })
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
        articlesAdapter.notifyItemChanged(adapterPosition, selectedArticle)

        Snackbar.make(
            dataBinding.root,
            getString(R.string.developer_msg),
            Snackbar.LENGTH_SHORT
        ).show()
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