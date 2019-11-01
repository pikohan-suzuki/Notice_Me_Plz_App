package com.amebaownd.pikohan_nwiatori.noticemeplz.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "message_table",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class Message(
    @PrimaryKey
    @ColumnInfo(name="message_id")
    val messageId:String=UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id")
    val id: String,
    @ColumnInfo(name = "timeStump")
    val timeStump: Long = Date().time,
    @ColumnInfo(name = "service_code")
    val service_code: Int
)