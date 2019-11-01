package com.amebaownd.pikohan_nwiatori.noticemeplz.data.model

import androidx.room.Embedded
import androidx.room.Relation

class UserAndMessage {
    @Embedded
    lateinit var user:User

    @Relation(parentColumn = "user_id",entityColumn = "user_id")
    lateinit var message:List<Message>
}