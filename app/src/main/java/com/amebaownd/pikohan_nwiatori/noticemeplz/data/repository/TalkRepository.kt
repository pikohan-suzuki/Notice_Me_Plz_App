package com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository

import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.TalkDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndMessage

class TalkRepository (private val messageDao: TalkDao){

    fun loadItem(userId:String):UserAndMessage{
        return messageDao.getById(userId)
    }


    suspend fun insertMessage(message:Message){
        messageDao.insert(message)
    }
}