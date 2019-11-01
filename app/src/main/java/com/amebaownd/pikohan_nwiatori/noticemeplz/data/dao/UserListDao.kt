package com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService

@Dao
interface UserListDao {

    @Query("SELECT * FROM user_table ORDER BY recently_talked")
    fun getAll():List<UserAndUsingService>

    @Query("SELECT * FROM user_table WHERE name LIKE (:key)")
    fun getByKey(key:String):List<UserAndUsingService>

}