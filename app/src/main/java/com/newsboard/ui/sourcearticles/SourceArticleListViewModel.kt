package com.newsboard.ui.sourcearticles

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.utils.ApiParams
import com.newsboard.utils.base.ResponseState

class SourceArticleListViewModel : ViewModel() {
    val sourceArticlesLiveData: MutableLiveData<ResponseState<ArticlesResponse>> = MutableLiveData()

    private val sourceArticleListRepo = SourceArticleListRepo()

    fun getSourceArticles(categoryName: Array<String?>) {
        sourceArticlesLiveData.value = ResponseState.Loading
        val paramsMap = mapOf(
            ApiParams.SOURCES to categoryName.joinToString(",")
        )
        sourceArticleListRepo.getSourceArticles(sourceArticlesLiveData, paramsMap)
    }
}