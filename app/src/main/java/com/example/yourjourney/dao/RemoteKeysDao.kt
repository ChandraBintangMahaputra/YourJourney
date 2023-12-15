package com.example.yourjourney.dao

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yourjourney.data.dcRemoteKeys

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<dcRemoteKeys>)

    @Query("SELECT * FROM tbl_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): dcRemoteKeys?

    @Query("DELETE FROM tbl_remote_keys")
    suspend fun deleteRemoteKeys()

}