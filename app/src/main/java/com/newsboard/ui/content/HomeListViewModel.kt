package com.newsboard.ui.content

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.newsboard.data.models.articles.Article
import com.newsboard.datasources.ArticlesDataSourceFactory
import com.newsboard.datasources.ArticlesDataSources
import com.newsboard.utils.ApiParams
import com.newsboard.utils.base.ResponseState
import java.util.concurrent.Executors

class HomeListViewModel : ViewModel() {

    val topHeadlinesLiveData = MediatorLiveData<ResponseState<PagedList<Article>>>()
    private val executor = Executors.newFixedThreadPool(5)

    fun getTopHeadlines(categoryName: Array<String?>) {
        topHeadlinesLiveData.value = ResponseState.Loading

        val paramsMap = mutableMapOf<String, Any>(
            ApiParams.CATEGORY to categoryName.joinToString(",")
        )

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(ArticlesDataSources.defaultPageSize)
            .setPrefetchDistance(3)
            .setPageSize(ArticlesDataSources.defaultPageSize)
            .build()

        val articlesDataSourceFactory =
            ArticlesDataSourceFactory(topHeadlinesLiveData, paramsMap = paramsMap)

        val articlesLivePagedBuilder = LivePagedListBuilder(articlesDataSourceFactory, pagedListConfig)
            .setFetchExecutor(executor)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<Article>() {
                override fun onItemAtEndLoaded(itemAtEnd: Article) {

                }
            })
            .build()

        topHeadlinesLiveData.addSource(articlesLivePagedBuilder) {
            topHeadlinesLiveData.value = ResponseState.Success(it)
        }
    }
}