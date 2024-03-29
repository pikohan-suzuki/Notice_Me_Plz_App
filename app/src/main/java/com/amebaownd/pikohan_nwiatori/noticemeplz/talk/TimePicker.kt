package com.amebaownd.pikohan_nwiatori.noticemeplz.talk

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePick : DialogFragment(),TimePickerDialog.OnTimeSetListener{

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        return TimePickerDialog(activity,this,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true)
    }
    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        val c = Calendar.getInstance()
        val time = String.format("%02d:%02d",hour,minute)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent().putExtra("selectedTime",time))
    }
}