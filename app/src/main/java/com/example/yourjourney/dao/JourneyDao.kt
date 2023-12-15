package com.example.yourjourney.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yourjourney.database.JourneyDb

@Dao
interface JourneyDao {

    @Query("SELECT * FROM tbl_journey")
    fun getAllStories(): PagingSource<Int, JourneyDb>

    @Query("SELECT * FROM tbl_journey")
    fun getStoriesForWidget(): List<JourneyDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(vararg : JourneyDb)

    @Query("DELETE FROM tbl_journey")
    fun deleteAllStories()

}