package com.newsboard.utils

import com.newsboard.App.Companion.context
import com.newsboard.R

val tabMenuCategories by lazy {
    arrayOf(
        context.getString(R.string.general),
        context.getString(R.string.entertainment),
        context.getString(R.string.business),
        context.getString(R.string.health),
        context.getString(R.string.science),
        context.getString(R.string.sports),
        context.getString(R.string.technology)
    )
}

object ApiParams {
    const val SOURCES = "sources"
    const val API_KEY = "apiKey"
    const val CATEGORY = "category"
    const val QUERY = "q"
    const val LANGUAGE = "language"
    const val PAGE_SIZE = "pageSize"
    const val PAGE = "page"
}