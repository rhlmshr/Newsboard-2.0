package com.newsboard.datasources

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.newsboard.data.models.articles.Article
import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.data.remote.ApiManager
import com.newsboard.utils.ApiParams
import com.newsboard.utils.base.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ArticlesDataSources(
    private val articlesLiveData: MutableLiveData<ResponseState<PagedList<Article>>>,
    private val paramsMap: MutableMap<String, Any>,
    private val isHeadlinesArticles: Boolean = false
) : PageKeyedDataSource<Long, Article>() {

    companion object {
        const val defaultPageSize = 10
    }

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Article>
    ) {
        getArticles(pageNo = 1, pageSize = params.requestedLoadSize, resultCallback = {
            callback.onResult(it, null, 2)
        })
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Article>) {
        getArticles(
            pageNo = params.key.toInt(),
            pageSize = params.requestedLoadSize,
            resultCallback = {
                callback.onResult(it, params.key + 1)
            })
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Article>) {}

    private fun getArticles(
        pageNo: Int,
        pageSize: Int = defaultPageSize,
        resultCallback: (List<Article>) -> Unit
    ) {

        paramsMap[ApiParams.PAGE] = pageNo
        paramsMap[ApiParams.PAGE_SIZE] = pageSize

        val articlesCallback = object : Callback<ArticlesResponse> {
            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                if (t is SocketTimeoutException || t is UnknownHostException)
                    articlesLiveData.value = ResponseState.NoInternet
                else
                    articlesLiveData.value = ResponseState.Error(t.message!!)
            }

            override fun onResponse(
                call: Call<ArticlesResponse>,
                response: Response<ArticlesResponse>
            ) {
                val articlesResponse = response.body()
                if (articlesResponse?.articles.isNullOrEmpty())
                    articlesLiveData.value = ResponseState.NoData

                articlesResponse?.articles?.let {
                    resultCallback(it)
                }
            }
        }

        if (isHeadlinesArticles) ApiManager.getSourceArticles(paramsMap).enqueue(articlesCallback)
        else ApiManager.getHeadlinedArticles(paramsMap).enqueue(articlesCallback)
    }
}