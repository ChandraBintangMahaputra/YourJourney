package com.example.yourjourney.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yourjourney.database.Widget

@Dao
interface WidgetDao {

    @Query("SELECT * FROM tbl_widget")
    fun getAllWidgetContent(): List<Widget>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewWidgets(storyList: List<Widget>)

    @Query("DELETE FROM tbl_widget")
    suspend fun deleteAllWidgets()
}