package com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository

import android.content.Intent
import android.util.Log
import androidx.core.text.htmlEncode
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.Constants
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.dao.TalkDao
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndMessage
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class TalkRepository (private val messageDao: TalkDao){

    fun loadItem(userId:String):UserAndMessage{
        return messageDao.getById(userId)
    }

    fun loadUserAndUsingService(userId:String):UserAndUsingService{
        return messageDao.getUserAndUsingService(userId)
    }

    suspend fun insertMessage(message:Message){
        messageDao.insert(message)
    }

    suspend fun deleteUser(userId:String){
        messageDao.delete(userId)
    }

     fun sendSlackMessage(userName:String,text:String):Int{
        var resultCode :Int=-1
        val urlStr ="https://slack.com/api/chat.postMessage?token=${Constants.TOKEN}&channel=@${userName}&text=\"${text.htmlEncode()}\""
        var con:HttpURLConnection?=null
        try{
            val url = URL(urlStr)
            con = url.openConnection() as HttpURLConnection
            con.requestMethod="POST"
            con.instanceFollowRedirects=false
            con.doOutput=true

            con.readTimeout=10000
            con.connectTimeout=20000

            con.connect()

            val status = con.responseCode
            resultCode=con.responseCode
            Log.d("Send Slack Message",con.responseMessage)
            if(status==HttpURLConnection.HTTP_OK){

            }else{

            }
        }catch (e:Exception){
            Log.e("ERROR",e.toString())
        }finally {
            con?.disconnect()
        }
        return resultCode
    }

     fun sendMail(email:String,text:String):Int{
         val intent = Intent()
         intent.action = Intent.ACTION_SEND
         intent.type ="text/plain"

         intent.putExtra(Intent.EXTRA_EMAIL,arrayOf(email))
         intent.putExtra(Intent.EXTRA_SUBJECT,"Notice Me Plz")
         intent.putExtra(Intent.EXTRA_TEXT,text)

         MyContext.getContext().startActivity(intent)

         return HttpURLConnection.HTTP_OK
    }
}