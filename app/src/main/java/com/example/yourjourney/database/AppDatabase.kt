package com.example.yourjourney.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yourjourney.dao.JourneyDao
import com.example.yourjourney.dao.RemoteKeysDao
import com.example.yourjourney.dao.WidgetDao
import com.example.yourjourney.data.dcRemoteKeys


@Database(
    entities =[JourneyDb::class, dcRemoteKeys::class, Widget::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getStoryDao(): JourneyDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
    abstract fun getWidgetContentDao(): WidgetDao

}
