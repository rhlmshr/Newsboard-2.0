package com.newsboard.data.models.articles

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.newsboard.data.models.sources.Source
import com.newsboard.utils.TimeAgo
import com.newsboard.utils.tzSdFormat
import java.text.ParseException
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
    val urlToImage: String,
    @Ignore
    var bookMarked: Boolean
) {
    fun getPublishedAtAgo(): String {
        return try {
            TimeAgo.getTimeAgo((tzSdFormat.parse(publishedAt) as Date).time)
        } catch (parseException: ParseException) {
            ""
        }
    }
}