package com.newsboard.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.newsboard.data.models.sources.Source

@Dao
interface NewsItemDao {

    @Insert
    fun insertSource(vararg sources: Source)

    @Query("SELECT * FROM Source")
    fun getAllSources(): LiveData<List<Source>>

    @Query("SELECT * FROM Source WHERE id = :sourceId")
    fun getSource(sourceId: String): LiveData<Source>
}