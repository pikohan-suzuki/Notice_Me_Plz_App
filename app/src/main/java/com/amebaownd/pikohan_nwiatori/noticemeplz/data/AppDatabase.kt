package com.amebaownd.pikohan_nwiatori.noticemeplz.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
],version = 2)
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
                    .addMigrations(MIGRATE1_2)
//                    .addCallback()
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATE1_2 =object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("drop table message_table")
                database.execSQL(
                        "create table message_table(message_id TEXT NOT NULL PRIMARY KEY,user_id TEXT NOT NULL,message TEXT NOT NULL,timeStump INTEGER NOT NULL,service_code INTEGER NOT NULL,FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE ON UPDATE CASCADE)")
            }
        }
    }


}