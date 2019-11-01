package com.amebaownd.pikohan_nwiatori.noticemeplz.addEditUser

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amebaownd.pikohan_nwiatori.noticemeplz.MainActivity
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.AddEditRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.Event
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddEditUserViewModel(private val addEditRepository: AddEditRepository) : ViewModel() {

    private var _userAndUsingService = MutableLiveData<UserAndUsingService>()
    val userAndUsingService: LiveData<UserAndUsingService> = _userAndUsingService

    val name = MutableLiveData<String>()

    private var _usingServices = MutableLiveData<List<UsingService>>()
    val usingServices: LiveData<List<UsingService>> = _usingServices

    private var _submitEvent = MutableLiveData<Event<Boolean>>(Event(false))
    val submitEvent: LiveData<Event<Boolean>> = _submitEvent

    private var _isAbleToSave = MutableLiveData<Boolean>(false)
    val isAbleToSave: LiveData<Boolean> = _isAbleToSave

    private var _addServiceEvent = MutableLiveData<Event<Boolean>>(Event(false))
    val addServiceEvent: LiveData<Event<Boolean>> = _addServiceEvent

    private var _fabColor =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        MutableLiveData<Int>(MyContext.getContext().getColor(R.color.colorMessageBackground))
    } else {
        MutableLiveData<Int>(MyContext.getContext().resources.getColor(R.color.colorMessageBackground))
    }
    var fabColor:LiveData<Int> = _fabColor


    fun start(
        userId: String?,
        savedName: String?,
        savedUsingServices: List<UsingService>?,
        serviceCode: Int,
        address: String?
    ) {
        if (savedName != null || savedUsingServices != null || serviceCode != -1 || address != null) {
            name.value = savedName
            _usingServices.value=savedUsingServices
            if (serviceCode != -1 && address != null) {
                addService(serviceCode,address)
            }
            isAbleToSave()
        } else if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = addEditRepository.getById(userId)
                if (result != null) {
                    _userAndUsingService.postValue(result)
                    name.postValue(result.user.name)
                    _usingServices.postValue(result.usingService)
                    isAbleToSave()
                }
            }
        }
    }

    private fun addService(serviceCode: Int, address: String) {
        val newUsingService = UsingService(
            id = "",
            service_code = serviceCode,
            address = address
        )
        val newList = _usingServices.value?.plus(listOf(newUsingService)) ?: listOf(newUsingService)
        _usingServices.value=newList
    }

    fun onSubmitButtonClicked() {
        val user = User(name=this.name.value!!,recently_talked = 0)
        val usingServices = mutableListOf<UsingService>()
        _usingServices.value!!.forEach{
            usingServices.add(UsingService(id=user.id,service_code = it.service_code,address = it.address))
        }
        viewModelScope.launch(Dispatchers.IO){
            addEditRepository.insertUser(user)
            addEditRepository.insertUsingService(*usingServices.toTypedArray())
        }
        _submitEvent.value = Event(true)
    }

    fun onAddServiceButtonClicked() {
        _addServiceEvent.value = Event(true)
    }

    fun isAbleToSave(){
        _isAbleToSave.value =
            !name.value.isNullOrBlank() && !name.value.isNullOrEmpty() && !_usingServices.value.isNullOrEmpty()
        if(_isAbleToSave.value!!){
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