package com.newsboard.data.models.articles

import com.google.gson.annotations.SerializedName
import com.newsboard.data.models.sources.Source
import com.newsboard.utils.TimeAgo
import com.newsboard.utils.tzSdFormat
import java.util.*

data class Article(
    @SerializedName("author")
    val author: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("source")
    val source: Source,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val urlToImage: String
) {
    fun getPublishedAtAgo(): String {
        return TimeAgo.getTimeAgo((tzSdFormat.parse(publishedAt) as Date).time)
    }
}