package com.amebaownd.pikohan_nwiatori.noticemeplz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.text.htmlEncode
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext


class AlarmBroadcastReceiver() : BroadcastReceiver() {
    companion object{
        var before=System.currentTimeMillis()
    }
    override fun onReceive(context: Context?, intent: Intent?) {

        val now = System.currentTimeMillis()
        Log.d("Timer","Interval: ${now- before}")
        before = now

        Log.d("Timer","Timer end.")
        val context = Job() + Dispatchers.Default
        val scope = CoroutineScope(context)
        scope.launch {
            if (intent != null) {
                val serviceCode = intent.getIntExtra("serviceCode", -1)
                if (serviceCode != -1) {
                    if (serviceCode.and(Constants.SLACK_SERVICE_CODE) != 0) {
                        Log.d(
                            "Timer",
                            "userName:${intent.getStringExtra("userName")} ,text:${intent.getStringExtra(
                                "text"
                            )}"
                        )
                        sendSlackMessage(
                            intent.getStringExtra("addressOrUserName"),
                            intent.getStringExtra("text")
                        )
                    }
                    if (serviceCode.and(Constants.MAIL_SERVICE_CODE) != 0) {
                        sendMail(intent.getStringExtra("addressOrUserName"), intent.getStringExtra("text"))
                    }
                }
            }

            }
        }
    }

    private fun sendSlackMessage(userName: String, text: String): Int {
        var resultCode: Int = -1
        val urlStr =
            "https://slack.com/api/chat.postMessage?token=${Constants.TOKEN}&channel=@${userName}&text=\"${text.htmlEncode()}\""
        var con: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.instanceFollowRedirects = false
            con.doOutput = true

            con.readTimeout = 10000
            con.connectTimeout = 20000

            con.connect()

            val status = con.responseCode
            resultCode = con.responseCode
            Log.d("Send Slack Message", con.responseMessage)
            if (status == HttpURLConnection.HTTP_OK) {

            } else {

            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        } finally {
            con?.disconnect()
        }
        return resultCode
    }

    private fun sendMail(email:String,text:String){

        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Notice Me Plz")
            intent.putExtra(Intent.EXTRA_TEXT, text)

            MyContext.getContext().startActivity(intent)
        }catch(e:Exception){
            Log.e("ERROR",e.toString())
        }
    }
