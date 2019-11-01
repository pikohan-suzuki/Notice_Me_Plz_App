package com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository

import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.UserListDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService

class UserListRepository (private val userListDao: UserListDao){

    fun filterItem(regex: String):List<UserAndUsingService>{
        val key = "$regex%"
        return userListDao.getByKey(key)
    }

    fun loadItem():List<UserAndUsingService>{
        return userListDao.getAll()
    }
}