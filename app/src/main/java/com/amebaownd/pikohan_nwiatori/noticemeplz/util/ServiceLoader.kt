package com.amebaownd.pikohan_nwiatori.noticemeplz.util

import android.content.Context
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.AppDatabase
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.AddEditRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.TalkRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.UserListRepository

object ServiceLoader {
    private var database : AppDatabase? = null
    var addEditRepository:AddEditRepository? = null
    var talkRepository:TalkRepository?=null
    var userListRepository:UserListRepository?=null

    fun provideAddEditRepository(context:Context):AddEditRepository{
        synchronized(this){
            return addEditRepository
                ?: createAddEditRepository(
                    context
                )
        }
    }

    private fun createAddEditRepository(context:Context):AddEditRepository{
        val database = database
            ?: createDatabase(
                context
            )
        val result = AddEditRepository(database.addEditUserDao())
        addEditRepository = result
        return result
    }

    fun provideTalkRepository(context:Context):TalkRepository{
        synchronized(this){
            return talkRepository
                ?: createTalkRepository(
                    context
                )
        }
    }

    private fun createTalkRepository(context:Context):TalkRepository{
        val database = database
            ?: createDatabase(
                context
            )
        val result = TalkRepository(database.talkDao())
        talkRepository = result
        return result
    }

    fun provideUserListRepository(context:Context):UserListRepository{
        synchronized(this){
            return userListRepository
                ?: createUserListRepository(
                    context
                )
        }
    }

    private fun createUserListRepository(context:Context):UserListRepository{
        val database = database
            ?: createDatabase(
                context
            )
        val result = UserListRepository(database.userListDao())
        userListRepository = result
        return result
    }



    private fun createDatabase(context:Context):AppDatabase{
        val result = AppDatabase.getDatabase(context)
        database =result
        return result
    }
}