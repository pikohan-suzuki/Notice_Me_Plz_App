package com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository

import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.AddEditUserDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService

class AddEditRepository (private val addEditUserDao: AddEditUserDao){

    fun getById(userId:String):UserAndUsingService{
        return addEditUserDao.getById(userId)
    }

    suspend fun insertUser(user:User){
        addEditUserDao.insertUser(user)
    }
    suspend fun insertUsingService(vararg usingService: UsingService){
        addEditUserDao.insertUsingService(*usingService)
    }

    suspend fun updateUser(user: User){
        addEditUserDao.updateUser(user)
    }
}