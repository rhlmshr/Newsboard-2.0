package com.newsboard.ui.content

import androidx.lifecycle.MutableLiveData
import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.data.remote.ApiManager
import com.newsboard.utils.base.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HomeListRepo {
    fun getTopHeadlines(
        topHeadlinesLiveData: MutableLiveData<ResponseState<ArticlesResponse>>,
        paramsMap: Map<String, Any>
    ) {
        ApiManager.getHeadlinedArticles(paramsMap).enqueue(object : Callback<ArticlesResponse> {
            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                if (t is SocketTimeoutException || t is UnknownHostException)
                    topHeadlinesLiveData.value = ResponseState.NoInternet
                else
                    topHeadlinesLiveData.value = ResponseState.Error(t.message!!)
            }

            override fun onResponse(
                call: Call<ArticlesResponse>,
                response: Response<ArticlesResponse>
            ) {
                val articlesResponse = response.body()
                if (articlesResponse?.articles.isNullOrEmpty())
                    topHeadlinesLiveData.value = ResponseState.NoData
                else
                    topHeadlinesLiveData.value =
                        ResponseState.Success(articlesResponse as ArticlesResponse)
            }
        })
    }
}