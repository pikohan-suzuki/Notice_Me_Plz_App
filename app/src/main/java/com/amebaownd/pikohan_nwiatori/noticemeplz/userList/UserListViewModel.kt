package com.amebaownd.pikohan_nwiatori.noticemeplz.userList

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.UserListRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListViewModel(private val userListRepository: UserListRepository):ViewModel(){

    private var _userAndUsingService = MutableLiveData<List<UserAndUsingService>>()
    val userAndUsingService : LiveData<List<UserAndUsingService>> = _userAndUsingService

    private var _startTalkEvent = MutableLiveData<Event<User>>()
    val startTalkEvent :LiveData<Event<User>> = _startTalkEvent

    private var _noResultText= MutableLiveData<String>()
    val noResultText:LiveData<String> = _noResultText

    private var _noUserVisibility=MutableLiveData<Int>(View.VISIBLE)
    val noUserVisibility :LiveData<Int> = _noUserVisibility

    fun start(){
        viewModelScope.launch(Dispatchers.IO){
            val result = userListRepository.loadItem()
            _userAndUsingService.postValue(result)
            if(result.isEmpty()) {
                _noUserVisibility.postValue(View.VISIBLE)
                _noResultText.postValue("ユーザを追加してください!")
            } else
                _noUserVisibility.postValue(View.GONE)
        }
    }

    fun filterItem(key:String){
        viewModelScope.launch(Dispatchers.IO){
            val result = userListRepository.filterItem(key)
            _userAndUsingService.postValue(result)
            if(result.isEmpty()) {
                _noUserVisibility.postValue(View.VISIBLE)
                _noResultText.postValue("\"$key\"に該当するユーザは見つかりませんでした。")
            } else
                _noUserVisibility.postValue(View.GONE)
        }
    }

    fun onItemClicked(user:User){
        _startTalkEvent.value = Event(user)
    }
}