package com.newsboard.ui.content

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.utils.ApiParams
import com.newsboard.utils.base.ResponseState

class HomeListViewModel : ViewModel() {

    var topHeadlinesLiveData: MutableLiveData<ResponseState<ArticlesResponse>> = MutableLiveData()

    private val homeListRepo = HomeListRepo()

    fun getTopHeadlines(categoryName: Array<String?>) {
        topHeadlinesLiveData.value = ResponseState.Loading
        val paramsMap = mapOf(
            ApiParams.CATEGORY to categoryName.joinToString(",")
        )
        homeListRepo.getTopHeadlines(topHeadlinesLiveData, paramsMap)
    }
}