package com.newsboard.datasources

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.newsboard.data.models.articles.Article
import com.newsboard.utils.base.ResponseState

class ArticlesDataSourceFactory(
    private val articlesLiveData: MutableLiveData<ResponseState<PagedList<Article>>>,
    private val paramsMap: MutableMap<String, Any>
) : DataSource.Factory<Long, Article>() {

    override fun create(): DataSource<Long, Article> {
        return ArticlesDataSources(articlesLiveData, paramsMap)
    }
}