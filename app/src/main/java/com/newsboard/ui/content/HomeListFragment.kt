package com.newsboard.ui.content

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.newsboard.R
import com.newsboard.adapters.ArticlesAdapter
import com.newsboard.data.models.articles.Article
import com.newsboard.databinding.FragmentHomeListBinding
import com.newsboard.utils.base.BaseFragment
import com.newsboard.utils.base.ResponseState

class HomeListFragment : BaseFragment<FragmentHomeListBinding>(),
    ArticlesAdapter.ArticleActionHandler {

    override val layoutId: Int
        get() = R.layout.fragment_home_list

    private lateinit var homeListViewModel: HomeListViewModel
    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeListViewModel.getTopHeadlines(arrayOf(arguments?.getString(CATEGORY_NAME)))
    }

    override fun init() {
        articlesAdapter = ArticlesAdapter(this)
    }

    override fun initLiveData() {
        homeListViewModel = ViewModelProvider(this).get(HomeListViewModel::class.java)
    }

    override fun setupViews() {
        dataBinding.listStates.rvList.adapter = articlesAdapter
        dataBinding.listStates.rvList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun setListeners() {
        dataBinding.viewModel = homeListViewModel
        dataBinding.lifecycleOwner = this

        homeListViewModel.topHeadlinesLiveData.observe(this, Observer {
            when (it) {
                is ResponseState.Success -> {
                    articlesAdapter.submitList(it.output)
                    dataBinding.pbLoader.hide()
                    dataBinding.listStates.vsState.visibility = View.VISIBLE
                    dataBinding.listStates.vsState.displayedChild = 0
                }

                else -> {
                    if (it is ResponseState.Loading) {
                        dataBinding.listStates.vsState.visibility = View.INVISIBLE
                        dataBinding.pbLoader.show()
                    } else {
                        dataBinding.pbLoader.hide()
                        dataBinding.listStates.vsState.visibility = View.VISIBLE
                        dataBinding.listStates.vsState.displayedChild = 1
                        setEmptyErrorStates(it, dataBinding.listStates.errorState.ivError, dataBinding.listStates.errorState.ivErrorTitle, dataBinding.listStates.errorState.ivErrorDesc)
                    }
                }
            }
        })
    }

    companion object {

        private const val CATEGORY_NAME = "category_name"

        fun newInstance(categoryName: String) = HomeListFragment().apply {
            arguments = bundleOf(
                CATEGORY_NAME to categoryName
            )
        }
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