package com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndMessage

@Dao
interface TalkDao {

    @Query("SELECT * FROM user_table WHERE user_id =(:userId) ORDER BY recently_talked")
    fun getById(userId:String):UserAndMessage

    @Insert
    suspend  fun insert(vararg message: Message)
}