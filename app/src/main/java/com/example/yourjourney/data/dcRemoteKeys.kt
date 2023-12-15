package com.example.yourjourney.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_remote_keys")
data class dcRemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
