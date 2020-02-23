package com.newsboard.data.remote

import com.newsboard.BuildConfig
import com.newsboard.data.models.articles.ArticlesResponse
import com.newsboard.data.models.sources.SourcesResponse
import com.newsboard.utils.ApiParams
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiManager {

    private val authenticatedRetrofitService: ApiInterface
        get() {
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = retrofitBuilder.client(httpClient).build()
            return retrofit.create(ApiInterface::class.java)
        }

    private val httpClient: OkHttpClient
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            return OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(20000, TimeUnit.MILLISECONDS)
                .build()
        }

    private val apiKeyInterceptor = Interceptor { chain ->
        val url =
            chain.request().url.newBuilder()
                .addQueryParameter(ApiParams.API_KEY, BuildConfig.NEWS_API_KEY)
                .addQueryParameter(ApiParams.LANGUAGE, "en") // todo make this dynamic
                .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        chain.proceed(request)
    }

    fun getHeadlinedArticles(paramsMap: Map<String, Any>): Call<ArticlesResponse> {
        return authenticatedRetrofitService.getHeadlinedArticles(paramsMap)
    }

    fun getSourceArticles(paramsMap: Map<String, Any>): Call<ArticlesResponse> {
        return authenticatedRetrofitService.getSourceArticles(paramsMap)
    }

    fun getSources(): Call<SourcesResponse> {
        return authenticatedRetrofitService.getSources()
    }
}