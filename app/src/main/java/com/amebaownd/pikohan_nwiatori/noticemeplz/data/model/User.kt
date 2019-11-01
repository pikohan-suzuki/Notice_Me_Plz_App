package com.amebaownd.pikohan_nwiatori.noticemeplz.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user_table")
data class User (
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id:String = UUID.randomUUID().toString(),
    @ColumnInfo(name="name")
    val name:String,
    @ColumnInfo(name="recently_talked")
    val recently_talked:Long
)