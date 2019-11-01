package com.amebaownd.pikohan_nwiatori.noticemeplz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amebaownd.pikohan_nwiatori.noticemeplz.addEditUser.AddEditUserViewModel
import com.amebaownd.pikohan_nwiatori.noticemeplz.chooseService.ChooseServiceViewModel
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.AddEditRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.TalkRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.UserListRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.talk.TalkViewModel
import com.amebaownd.pikohan_nwiatori.noticemeplz.userList.UserListViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory (
    private val addEditRepository: AddEditRepository,
    private val talkRepository: TalkRepository,
    private val userListRepository: UserListRepository
):ViewModelProvider.NewInstanceFactory(){

    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        val t = with(modelClass) {
            when {
                isAssignableFrom(UserListViewModel::class.java) ->
                    UserListViewModel(userListRepository)
                isAssignableFrom(TalkViewModel::class.java) ->
                    TalkViewModel(talkRepository)
                isAssignableFrom(AddEditUserViewModel::class.java) ->
                    AddEditUserViewModel(addEditRepository)
                isAssignableFrom(ChooseServiceViewModel::class.java)->
                    ChooseServiceViewModel()
                else ->
                    throw IllegalArgumentException("Unknown ViewModelClass $modelClass")
            }
        } as T
        return t
    }
}