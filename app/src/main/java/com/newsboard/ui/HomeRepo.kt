package com.newsboard.ui

import androidx.lifecycle.MediatorLiveData
import com.newsboard.data.local.LocalDataManager
import com.newsboard.data.models.sources.Source
import com.newsboard.data.models.sources.SourcesResponse
import com.newsboard.data.remote.ApiManager
import com.newsboard.utils.base.ResponseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HomeRepo {

    fun getSources(sourcesLiveData: MediatorLiveData<ResponseState<List<Source>>>) {
        ApiManager.getSources().enqueue(object : Callback<SourcesResponse> {
            override fun onFailure(call: Call<SourcesResponse>, t: Throwable) {
                if (t is SocketTimeoutException || t is UnknownHostException)
                    sourcesLiveData.value = ResponseState.NoInternet
                else
                    sourcesLiveData.value = ResponseState.Error(t.message!!)
            }

            override fun onResponse(
                call: Call<SourcesResponse>,
                response: Response<SourcesResponse>
            ) {
                val articlesResponse = response.body()
                if (articlesResponse?.sources.isNullOrEmpty())
                    sourcesLiveData.value = ResponseState.NoData
                else
                    LocalDataManager.insertSource(*articlesResponse?.sources!!.toTypedArray())
            }
        })
    }
}