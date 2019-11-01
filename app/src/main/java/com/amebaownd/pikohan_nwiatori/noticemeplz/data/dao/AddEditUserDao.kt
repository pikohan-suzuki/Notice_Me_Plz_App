package com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao

import androidx.room.*
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService

@Dao
interface AddEditUserDao {

    @Query("SELECT * FROM user_table WHERE user_id=(:userId)")
    fun getById(userId:String):UserAndUsingService

    @Update
    suspend fun updateUser(user:User)

    @Update
    suspend fun updateUsingService(vararg usingService: UsingService)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user:User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsingService(vararg usingService: UsingService)

    @Delete
    suspend fun  deleteUser(user:User)

    @Delete
    suspend fun deleteUsingService(usingService: UsingService)
}