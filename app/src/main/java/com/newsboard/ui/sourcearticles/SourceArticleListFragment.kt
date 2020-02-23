package com.newsboard.ui.sourcearticles

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.newsboard.R
import com.newsboard.adapters.ArticlesAdapter
import com.newsboard.databinding.FragmentSourceArticleListBinding
import com.newsboard.utils.base.BaseFragment
import com.newsboard.utils.base.ResponseState
import kotlinx.android.synthetic.main.layout_empty_error_state.*
import kotlinx.android.synthetic.main.layout_list_with_states.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class SourceArticleListFragment : BaseFragment<FragmentSourceArticleListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_source_article_list

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var sourceArticleListViewModel: SourceArticleListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            sourceArticleListViewModel.getSourceArticles(arrayOf(it.getString("source_id")))
        }
    }

    override fun init() {
        articlesAdapter = ArticlesAdapter()
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
                    articlesAdapter.submitList(it.output.articles)
                }

                else -> {
                    if (it is ResponseState.Loading) {
                        vs_state.visibility = View.INVISIBLE
                        dataBinding.pbLoader.show()
                    } else {
                        dataBinding.pbLoader.hide()
                        vs_state.visibility = View.VISIBLE
                        vs_state.displayedChild = 1
                        setEmptyErrorStates(it, iv_error, iv_error_title, iv_error_desc)
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
}