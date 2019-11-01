package com.amebaownd.pikohan_nwiatori.noticemeplz.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.AddEditUserDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.TalkDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.UserListDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService

@Database(entities = [
    Message::class,
    User::class,
    UsingService::class
],version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun addEditUserDao():AddEditUserDao
    abstract fun talkDao():TalkDao
    abstract fun userListDao():UserListDao

    companion object{
        @Volatile
        private var INSTANCE :AppDatabase?=null

        fun getDatabase(
            context: Context
        ):AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null)
                return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
//                    .addMigrations()
//                    .addCallback()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}