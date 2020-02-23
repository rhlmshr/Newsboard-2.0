package com.newsboard.data.local

import androidx.lifecycle.LiveData
import com.newsboard.data.models.sources.Source
import java.util.concurrent.Executors

object LocalDataManager {

    private val executor = Executors.newSingleThreadExecutor()

    fun insertSource(vararg sources: Source) {
        executor.execute { AppDatabase.instance?.newsItemDao?.insertSource(*sources) }
    }

    fun getAllSources(): LiveData<List<Source>> {
        return AppDatabase.instance?.newsItemDao?.getAllSources()!!
    }

    fun getSource(sourceId: String): LiveData<Source>? {
        return AppDatabase.instance?.newsItemDao?.getSource(sourceId)
    }
}