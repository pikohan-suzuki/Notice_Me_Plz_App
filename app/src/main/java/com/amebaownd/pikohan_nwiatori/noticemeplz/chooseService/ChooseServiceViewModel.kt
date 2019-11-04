package com.amebaownd.pikohan_nwiatori.noticemeplz.chooseService

import android.os.Build
import android.view.View
import androidx.core.view.children
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.Constants
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.Event
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.PxDpConverter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ChooseServiceViewModel() : ViewModel() {

    private var _isAbleToSubmit = MutableLiveData<Boolean>(false)
    val isAbleToSubmit: LiveData<Boolean> = _isAbleToSubmit

    private var _selectedService = MutableLiveData<Int>()
    val selectedService: LiveData<Int> = _selectedService

    private var _submitEvent = MutableLiveData<Event<Boolean>>()
    val submitEvent: LiveData<Event<Boolean>> = _submitEvent

    private var _hint = MutableLiveData<String>("Address")
    val hint:LiveData<String> = _hint

    private var _fabColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        MutableLiveData<Int>(MyContext.getContext().getColor(R.color.colorMessageBackground))
    } else {
        MutableLiveData<Int>(MyContext.getContext().resources.getColor(R.color.colorMessageBackground))
    }
    val fabColor:LiveData<Int> = _fabColor

    var address = MutableLiveData<String>()


    fun onChipChose(view: View) {
        when (view.id) {
            R.id.chooseService_sms_chip -> {
                _selectedService.value = Constants.MAIL_SERVICE_CODE
                _hint.value = "Address"
            }
            R.id.chooseService_slack_chip -> {
                _selectedService.value = Constants.SLACK_SERVICE_CODE
                _hint.value = "User Name"
            }
            else ->
                return
        }
        (view as Chip).chipStrokeWidth = PxDpConverter.dp2Px(3f, MyContext.getContext())
        (view.parent as ChipGroup).children.forEach {
            if (it.id != view.id) {
                val chip = it as Chip
                chip.isChecked = false
                chip.chipStrokeWidth = 0f
            }
        }
        isAbleToSubmit()
    }

    fun onAddressChanged() {
            isAbleToSubmit()
    }

    fun onFabClicked() {
        _submitEvent.value = Event(true)
    }

    private fun isAbleToSubmit() {
        _isAbleToSubmit.value =
            _selectedService.value != null && !address.value.isNullOrBlank() && !address.value.isNullOrEmpty()
        if(_isAbleToSubmit.value!!){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _fabColor.value = MyContext.getContext().getColor(R.color.colorPrimary)
            }else{
                _fabColor.value = MyContext.getContext().resources.getColor(R.color.colorPrimary)
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _fabColor.value = MyContext.getContext().getColor(R.color.colorMessageBackground)
            }else{
                _fabColor.value = MyContext.getContext().resources.getColor(R.color.colorMessageBackground)
            }
        }
    }
}