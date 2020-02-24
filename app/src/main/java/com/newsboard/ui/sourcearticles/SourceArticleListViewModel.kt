package com.newsboard.ui.sourcearticles

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

class SourceArticleListViewModel : ViewModel() {

    val sourceArticlesLiveData = MediatorLiveData<ResponseState<PagedList<Article>>>()

    private val executor = Executors.newFixedThreadPool(5)

    fun getSourceArticles(categoryName: Array<String?>, searchQuery: String = "") {
        sourceArticlesLiveData.value = ResponseState.Loading
        val paramsMap = mutableMapOf<String, Any>(
            ApiParams.SOURCES to categoryName.joinToString(",")
        )

        if (!searchQuery.isBlank()) paramsMap[ApiParams.QUERY] = searchQuery

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(ArticlesDataSources.defaultPageSize)
            .setPrefetchDistance(3)
            .setPageSize(ArticlesDataSources.defaultPageSize)
            .build()

        val articlesDataSourceFactory =
            ArticlesDataSourceFactory(
                sourceArticlesLiveData,
                paramsMap = paramsMap,
                isHeadlinesArticles = true
            )

        sourceArticlesLiveData.addSource(
            LivePagedListBuilder(articlesDataSourceFactory, pagedListConfig)
                .setFetchExecutor(executor)
                .build()
        ) {
            sourceArticlesLiveData.value = ResponseState.Success(it)
        }
    }
}