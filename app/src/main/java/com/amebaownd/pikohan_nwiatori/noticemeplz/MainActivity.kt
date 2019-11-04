package com.amebaownd.pikohan_nwiatori.noticemeplz

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import kotlinx.android.synthetic.main.activity_main.*
import android.os.StrictMode

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        MyContext.onCreateAppcalition(this)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val navController:NavController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration=
            AppBarConfiguration.Builder(R.id.userListFragment).build()
        setupActionBarWithNavController(navController,appBarConfiguration)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun setTimer(addressOrUserName:String,text:String,time:String,serviceCode: Int){
        val splitedTime = time.split(":")
        val addedTime =splitedTime[0].toInt()*3600000 + splitedTime[1].toInt()*60000
        val alarmTime = System.currentTimeMillis()+addedTime

        val alarmIntent = Intent(this, AlarmBroadcastReceiver::class.java)
        alarmIntent.putExtra("addressOrUserName",addressOrUserName)
        alarmIntent.putExtra("text",text)
        alarmIntent.putExtra("serviceCode",serviceCode)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    fun setRepeatTimer(addressOrUserName:String,text:String,firstTime:Long,interval:Long,serviceCode:Int){
        val alarmIntent = Intent(this,AlarmBroadcastReceiver::class.java)
        alarmIntent.putExtra("addressOrUserName",addressOrUserName)
        alarmIntent.putExtra("text",text)
        alarmIntent.putExtra("serviceCode",serviceCode)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setRepeating(AlarmManager.RTC_WAKEUP,firstTime,interval,pendingIntent)
    }

    fun stopAlarmService(){
        val intent = Intent(this,AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getService(this,0,intent,0)

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if(alarmManager != null)
                alarmManager.cancel(pendingIntent)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopAlarmService()
    }
}