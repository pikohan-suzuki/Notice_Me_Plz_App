package com.amebaownd.pikohan_nwiatori.noticemeplz.talk

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.Constants
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.TalkRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.Event
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.PxDpConverter
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.HttpURLConnection

class TalkViewModel(private val talkRepository: TalkRepository) : ViewModel() {

    private var _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private var _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private var _userAndUsingServices = MutableLiveData<UserAndUsingService>()
    val userAndUsingServices: LiveData<UserAndUsingService> = _userAndUsingServices

    private var _isAbleToSend = MutableLiveData<Boolean>(false)
    val isAbleToSend: LiveData<Boolean> = _isAbleToSend

    var selectedServices = listOf<Int>()

    var isRepeatChipChecked = MutableLiveData<Boolean>(false)
    var isTimerChipChecked = MutableLiveData<Boolean>(false)

    private var _isRepeat = MutableLiveData<Boolean>(false)
    val isRepeat: LiveData<Boolean> = _isRepeat

    private var _setRepeatSlackEvent = MutableLiveData<Event<String>>(Event(""))
    val setRepeatSlackEvent :LiveData<Event<String>> = _setRepeatSlackEvent

    private var _setRepeatMailEvent = MutableLiveData<Event<String>>(Event(""))
    val setRepeatMailEvent :LiveData<Event<String>> = _setRepeatMailEvent

    var startTime = MutableLiveData<String>("")
    var interval = MutableLiveData<String>("")

    private var _isTimer = MutableLiveData<Boolean>(false)
    val isTimer: LiveData<Boolean> = _isTimer

    private var _setTimerSlackEvent = MutableLiveData<Event<String>>(Event(""))
    val setTimerSlackEvent :LiveData<Event<String>> = _setTimerSlackEvent

    private var _setTimerMailEvent = MutableLiveData<Event<String>>(Event(""))
    val setTimerMailEvent :LiveData<Event<String>> = _setTimerMailEvent

    var time = MutableLiveData<String>("")

    private var _repeatVisibility = MutableLiveData<Int>(View.GONE)
    val repeatVisibility: LiveData<Int> = _repeatVisibility

    private var _timerVisibility = MutableLiveData<Int>(View.GONE)
    val timerVisibility: LiveData<Int> = _timerVisibility

    private var _noMessageExplainVisibility = MutableLiveData<Int>(View.GONE)
    val noMessageExplainVisibility: LiveData<Int> = _noMessageExplainVisibility

    private var _fabColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        MutableLiveData<Int>(MyContext.getContext().getColor(R.color.colorMessageBackground))
    } else {
        MutableLiveData<Int>(MyContext.getContext().resources.getColor(R.color.colorMessageBackground))
    }
    var fabColor: LiveData<Int> = _fabColor

    var message = MutableLiveData<String>()

    fun start(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = talkRepository.loadItem(userId)
            _user.postValue(result.user)
            _messages.postValue(result.message)
            val userAndUsingServiceResult = talkRepository.loadUserAndUsingService(userId)
            _userAndUsingServices.postValue(userAndUsingServiceResult)
            setExplainVisibility(result.message.size)
        }
    }

    fun delete(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            talkRepository.deleteUser(userId)
        }
    }

    fun onSendButtonClicked() {
        if (_isAbleToSend.value!!) {
            if (_user.value != null) {
                var serviceCode = 0
                selectedServices.forEach {
                    serviceCode = serviceCode or it
                }
                val newMessage = Message(
                    id = user.value!!.id,
                    message = message.value!!,
                    service_code = serviceCode
                )
                if (_isTimer.value!!.not() && _isRepeat.value!!.not()) {
                    val newMessageList = if (_messages.value != null) {
                        _messages.value!! + listOf(newMessage)
                    } else {
                        listOf(newMessage)
                    }
                    _messages.value = newMessageList
                    viewModelScope.launch(Dispatchers.IO) {
                        talkRepository.insertMessage(newMessage)
                        if (selectedServices.contains(Constants.SLACK_SERVICE_CODE)) {
                            val slackService =
                                _userAndUsingServices.value?.usingService?.filter { it.service_code == Constants.SLACK_SERVICE_CODE }
                            if (!slackService.isNullOrEmpty()) {
                                val resultCode = talkRepository.sendSlackMessage(
                                    slackService[0].address,
                                    message.value!!
                                )
                                if (resultCode == HttpURLConnection.HTTP_OK) {
                                    message.postValue("")
                                    Log.d("Send Slack Message", "Success")
                                } else {
                                    Toast.makeText(
                                        MyContext.getContext().applicationContext,
                                        "Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("Send Slack Message", "failed")
                                }
                            }
                        }

                        if(selectedServices.contains(Constants.MAIL_SERVICE_CODE)){
                            val mailService =
                            _userAndUsingServices.value?.usingService?.filter { it.service_code == Constants.MAIL_SERVICE_CODE }
                            if (!mailService.isNullOrEmpty()) {
                                val resultCode = talkRepository.sendMail(
                                    mailService[0].address,
                                    message.value!!
                                )
                                if (resultCode == HttpURLConnection.HTTP_OK) {
                                    message.postValue("")
                                    Log.d("Send Mail", "Success")
                                } else {
                                    Log.d("Send Mail Message", "failed")
                                }
                            }
                        }
                        setExplainVisibility(1)
                    }
                    Toast.makeText(MyContext.getContext().applicationContext,"メッセージを送信しました",Toast.LENGTH_SHORT).show()
                } else if (isRepeatChipChecked.value!!) {
                    _setRepeatSlackEvent.value = Event("b1702996")
                    if(repeatValidation()){
                        val newMessageList = if (_messages.value != null) {
                            _messages.value!! + listOf(newMessage)
                        } else {
                            listOf(newMessage)
                        }
                        _messages.value = newMessageList

                        viewModelScope.launch(Dispatchers.IO){
                            talkRepository.insertMessage(newMessage)
                        }
                        Toast.makeText(MyContext.getContext().applicationContext,"${startTime.value} 秒後から ${interval.value}秒おきにメッセージを送信します",Toast.LENGTH_SHORT).show()

                        if (selectedServices.contains(Constants.SLACK_SERVICE_CODE)) {
                            val slackService =
                                _userAndUsingServices.value?.usingService?.filter { it.service_code == Constants.SLACK_SERVICE_CODE }
                            if (!slackService.isNullOrEmpty()) {
                                _setRepeatSlackEvent.value = Event(slackService[0].address)
                            }
                        }
                        if(selectedServices.contains(Constants.MAIL_SERVICE_CODE)){
                            val mailService =
                                _userAndUsingServices.value?.usingService?.filter { it.service_code == Constants.MAIL_SERVICE_CODE }
                            if (!mailService.isNullOrEmpty()) {
                                _setRepeatMailEvent.value = Event(mailService[0].address)
                            }
                        }
                        message.value=""
                        startTime.value=""
                        interval.value=""
                    }

                } else if (isTimerChipChecked.value!!) {
                    if(timerValidation()) {
                        val newMessageList = if (_messages.value != null) {
                            _messages.value!! + listOf(newMessage)
                        } else {
                            listOf(newMessage)
                        }
                        _messages.value = newMessageList

                        viewModelScope.launch(Dispatchers.IO){
                            talkRepository.insertMessage(newMessage)
                        }

                        Toast.makeText(MyContext.getContext().applicationContext,"${time.value} 後にメッセージを送信します",Toast.LENGTH_SHORT).show()
                        if (selectedServices.contains(Constants.SLACK_SERVICE_CODE)) {
                            val slackService =
                                _userAndUsingServices.value?.usingService?.filter { it.service_code == Constants.SLACK_SERVICE_CODE}
                            if (!slackService.isNullOrEmpty()) {
                                _setTimerSlackEvent.value = Event(slackService[0].address)
                            }
                        }
                        if (selectedServices.contains(Constants.MAIL_SERVICE_CODE)) {
                            val slackService =
                                _userAndUsingServices.value?.usingService?.filter { it.service_code == Constants.MAIL_SERVICE_CODE}
                            if (!slackService.isNullOrEmpty()) {
                                _setTimerMailEvent.value = Event(slackService[0].address)
                            }
                        }
                        message.value=""
                        time.value=null
                    }
                }
            }
        }
    }

    fun onChipSelected(view: View) {
        val chip = view as Chip
        val mSelectedServices = selectedServices.toMutableList()
        val serviceCode = when (chip.id) {
            R.id.talk_mail_chip ->
                Constants.MAIL_SERVICE_CODE
            R.id.talk_slack_chip ->
                Constants.SLACK_SERVICE_CODE
            else ->
                throw IllegalArgumentException("Unknown chip ${chip.id}is selected")
        }
        if (!mSelectedServices.contains(serviceCode))
            mSelectedServices.add(serviceCode)
        else
            mSelectedServices.remove(serviceCode)

        when (chip.id) {
            R.id.talk_mail_chip -> {
                if (mSelectedServices.contains(Constants.MAIL_SERVICE_CODE))
                    chip.chipStrokeWidth = PxDpConverter.dp2Px(3f, MyContext.getContext())
                else
                    chip.chipStrokeWidth = 0f
            }
            R.id.talk_slack_chip -> {
                if (mSelectedServices.contains(Constants.SLACK_SERVICE_CODE))
                    chip.chipStrokeWidth = PxDpConverter.dp2Px(3f, MyContext.getContext())
                else
                    chip.chipStrokeWidth = 0f
            }
            else ->
                throw IllegalArgumentException("Unknown chip ${chip.id}is selected")
        }

        selectedServices = mSelectedServices.toList()
        isAbleToSend()
    }

    fun onOnOffChipClicked(view: View) {
        val chip = view as Chip
        when (chip.id) {
            R.id.talk_repeat_chip -> {
                if (!isRepeatChipChecked.value!!) {
                    chip.setChipBackgroundColorResource(R.color.colorPrimary)
                    setRepeatOn()
                    setTimerOff()
                } else {
                    chip.setChipBackgroundColorResource(R.color.colorMessageBackground)
                    setRepeatOff()
                }
            }
            R.id.talk_timer_chip -> {
                if (!isTimerChipChecked.value!!) {
                    chip.setChipBackgroundColorResource(R.color.colorPrimary)
                    setTimerOn()
                    setRepeatOff()
                } else {
                    chip.setChipBackgroundColorResource(R.color.colorMessageBackground)
                    setTimerOff()
                }
            }
            else ->
                throw IllegalArgumentException("Unknown chip ${chip.id}is selected")
        }
        isAbleToSend()
    }

    private fun setRepeatOn(){
        _isRepeat.value = true
        isRepeatChipChecked.value = true
        _repeatVisibility.value = View.VISIBLE
    }
    private fun setRepeatOff(){
        _isRepeat.value = false
        isRepeatChipChecked.value = false
        _repeatVisibility.value = View.GONE
    }
    private fun setTimerOn(){
        _isTimer.value = true
        isTimerChipChecked.value = true
        _timerVisibility.value = View.VISIBLE
    }
    private fun setTimerOff(){
        _isTimer.value = false
        isTimerChipChecked.value = false
        _timerVisibility.value = View.GONE
    }


    fun onMessageChanged() {
        isAbleToSend()
    }
    fun onTimeChanged(){
        isAbleToSend()
    }
    fun onStartTimeChanged(){
        isAbleToSend()
    }
    fun onIntervalChanged(){
        isAbleToSend()
    }

//    private fun repeatValidation():Boolean{
//
//    }



    private fun timerValidation():Boolean{
        val split = time.value!!.split(":")
        var isCorrect = true
        if(split.size==2){
            try{
                if(split[0].length==2){
                    val hour = split[0].toInt()
                    if(hour<0)
                        isCorrect= false
                }else{
                    isCorrect= false
                }
                if(split[1].length==2){
                    val minutes = split[1].toInt()
                    if(minutes<0||minutes>59)
                        isCorrect= false
                }else{
                    isCorrect= false
                }
            }catch(e:Exception){
                Toast.makeText(MyContext.getContext().applicationContext,e.toString(),Toast.LENGTH_LONG).show()
                isCorrect= false
            }
        }else{
            isCorrect= false
        }
        if(!isCorrect)
            Toast.makeText(MyContext.getContext().applicationContext,"正しい `Time` を入力してください。",Toast.LENGTH_LONG).show()

        return isCorrect
    }

    private fun repeatValidation():Boolean{
        var isStartTimeCorrect = true
        var isIntervalCorrect = true
        if(startTime.value != null){
            if(startTime.value!!.toInt() <= 0)
                isStartTimeCorrect=false
        }else {
            isStartTimeCorrect = false
        }
        if(startTime.value != null){
            if(startTime.value!!.toInt() <= 0)
                isIntervalCorrect=false
        }else {
            isIntervalCorrect = false
        }

        if(!isIntervalCorrect && !isStartTimeCorrect)
            Toast.makeText(MyContext.getContext().applicationContext,"`Start Time` と `Interval`を正しく入力してください。",Toast.LENGTH_LONG).show()
        else if(!isIntervalCorrect)
            Toast.makeText(MyContext.getContext().applicationContext,"正しい `Interval を入力してください。",Toast.LENGTH_LONG).show()
        else if(!isStartTimeCorrect)
            Toast.makeText(MyContext.getContext().applicationContext,"正しい `Start Time` を入力してください。",Toast.LENGTH_LONG).show()

        return isIntervalCorrect && isStartTimeCorrect
    }
    private fun isAbleToSend() {
        _isAbleToSend.value =
            when {
                isRepeatChipChecked.value!! -> !message.value.isNullOrBlank() && !message.value.isNullOrEmpty() && selectedServices.isNotEmpty() && !startTime.value.isNullOrEmpty() && !interval.value.isNullOrEmpty()
                isTimerChipChecked.value!! -> !message.value.isNullOrBlank() && !message.value.isNullOrEmpty() && selectedServices.isNotEmpty() && !time.value.isNullOrEmpty()
                else -> !message.value.isNullOrBlank() && !message.value.isNullOrEmpty() && selectedServices.isNotEmpty()
            }

        if (_isAbleToSend.value!!) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _fabColor.value = MyContext.getContext().getColor(R.color.colorPrimary)
            } else {
                _fabColor.value = MyContext.getContext().resources.getColor(R.color.colorPrimary)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _fabColor.value = MyContext.getContext().getColor(R.color.colorMessageBackground)
            } else {
                _fabColor.value =
                    MyContext.getContext().resources.getColor(R.color.colorMessageBackground)
            }
        }
    }

    private fun setExplainVisibility(size: Int) {
        if (size > 0)
            _noMessageExplainVisibility.postValue(View.GONE)
        else
            _noMessageExplainVisibility.postValue(View.VISIBLE)
    }
}