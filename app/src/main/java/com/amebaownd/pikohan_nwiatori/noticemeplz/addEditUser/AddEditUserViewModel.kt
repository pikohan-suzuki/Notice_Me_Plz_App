package com.amebaownd.pikohan_nwiatori.noticemeplz.addEditUser

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.AddEditRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.Event
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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

    private var _fabColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        MutableLiveData<Int>(MyContext.getContext().getColor(R.color.colorMessageBackground))
    } else {
        MutableLiveData<Int>(MyContext.getContext().resources.getColor(R.color.colorMessageBackground))
    }
    var fabColor: LiveData<Int> = _fabColor


    fun start(
        userId: String?,
        user:User?,
        savedName: String?,
        savedUsingServices: List<UsingService>?,
        serviceCode: Int,
        address: String?
    ) {
        if(savedName!=null || savedUsingServices!=null || user!=null) {
            if (savedName != null)
                name.value = savedName
            if (savedUsingServices != null)
                _usingServices.value = savedUsingServices
            if (user != null) {
                _userAndUsingService.value = UserAndUsingService().apply {
                    this.user = user
                    _usingServices.value?.let {
                        this.usingService = it
                    }
                }
            }
            if (serviceCode != -1 && address != null) {
                addService(serviceCode, address)
            }
        }else if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = addEditRepository.getById(userId)
                if (result != null) {
                    _userAndUsingService.postValue(result)
                    name.postValue(result.user.name)
                    _usingServices.postValue(result.usingService)
                }
            }
        }
        isAbleToSave()
    }

    private fun addService(serviceCode: Int, address: String) {
        val newUsingService = UsingService(
            id = UUID.randomUUID().toString(),
            service_code = serviceCode,
            address = address
        )
        val newList = _usingServices.value?.plus(listOf(newUsingService)) ?: listOf(newUsingService)
        _usingServices.value = newList
    }

    fun onSubmitButtonClicked() {
        var newUser: User? = null
        newUser = if (_userAndUsingService.value?.user?.id != null)
            User(
                id = _userAndUsingService.value!!.user.id,
                name = name.value!!,
                recently_talked = _userAndUsingService.value!!.user.recently_talked
            )
        else
            User(name = this.name.value!!, recently_talked = 0)

        val usingServices = mutableListOf<UsingService>()
        _usingServices.value!!.forEach {
            usingServices.add(
                UsingService(
                    id = newUser.id,
                    service_code = it.service_code,
                    address = it.address
                )
            )
        }

        if(newUser.id == _userAndUsingService.value?.user?.id){
            viewModelScope.launch(Dispatchers.IO) {
                addEditRepository.updateUser(newUser)
                addEditRepository.deleteUsingServiceByUserId(newUser.id)
                addEditRepository.insertUsingService(*usingServices.toTypedArray())
            }
        }else{
            viewModelScope.launch(Dispatchers.IO) {
                addEditRepository.insertUser(newUser)
                addEditRepository.insertUsingService(*usingServices.toTypedArray())
            }
        }


        _submitEvent.value = Event(true)
    }

    fun onAddServiceButtonClicked() {
        _addServiceEvent.value = Event(true)
    }

    fun isAbleToSave() {
        _isAbleToSave.value =
            !name.value.isNullOrBlank() && !name.value.isNullOrEmpty() && !_usingServices.value.isNullOrEmpty()
        if (_isAbleToSave.value!!) {
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

    fun onDeleteService(usingService :UsingService){
        var newList =_usingServices.value
        if(newList!=null){
            newList = newList.toMutableList().filter {
                it.usingServiceId!=usingService.usingServiceId }
            _usingServices.value = newList.toList()
            isAbleToSave()
        }
    }
}