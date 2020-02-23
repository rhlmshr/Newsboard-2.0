package com.newsboard.data.remote

import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.data.models.sources.SourcesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface ApiInterface {

    @GET("/v2/top-headlines")
    fun getHeadlinedArticles(@QueryMap paramsMap: Map<String, Any>): Call<ArticlesResponse>

    @GET("/v2/everything")
    fun getSourceArticles(@QueryMap paramsMap: Map<String, Any>): Call<ArticlesResponse>

    @GET("/v2/sources")
    fun getSources(): Call<SourcesResponse>
}