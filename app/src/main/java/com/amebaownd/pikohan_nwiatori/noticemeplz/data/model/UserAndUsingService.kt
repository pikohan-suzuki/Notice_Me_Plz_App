package com.amebaownd.pikohan_nwiatori.noticemeplz.data.model

import androidx.room.Embedded
import androidx.room.Relation

class UserAndUsingService {
    @Embedded
    lateinit var user:User

    @Relation(parentColumn = "user_id",entityColumn = "user_id")
    lateinit var usingService:List<UsingService>
}