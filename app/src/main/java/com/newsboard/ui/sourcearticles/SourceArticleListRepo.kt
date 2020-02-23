package com.newsboard.ui.sourcearticles

import androidx.lifecycle.MutableLiveData
import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.data.remote.ApiManager
import com.newsboard.utils.base.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SourceArticleListRepo {

    fun getSourceArticles(
        sourceArticlesLiveData: MutableLiveData<ResponseState<ArticlesResponse>>,
        paramsMap: Map<String, Any>
    ) {
        ApiManager.getSourceArticles(paramsMap).enqueue(object : Callback<ArticlesResponse> {
            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                if (t is SocketTimeoutException || t is UnknownHostException)
                    sourceArticlesLiveData.value = ResponseState.NoInternet
                else
                    sourceArticlesLiveData.value = ResponseState.Error(t.message!!)
            }

            override fun onResponse(
                call: Call<ArticlesResponse>,
                response: Response<ArticlesResponse>
            ) {
                val articlesResponse = response.body()
                if (articlesResponse?.articles.isNullOrEmpty())
                    sourceArticlesLiveData.value = ResponseState.NoData
                else
                    sourceArticlesLiveData.value =
                        ResponseState.Success(articlesResponse as ArticlesResponse)
            }
        })
    }
}