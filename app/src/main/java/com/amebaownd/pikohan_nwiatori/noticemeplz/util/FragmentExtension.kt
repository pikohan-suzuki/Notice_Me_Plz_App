package com.amebaownd.pikohan_nwiatori.noticemeplz.util

import androidx.fragment.app.Fragment
import com.amebaownd.pikohan_nwiatori.noticemeplz.ViewModelFactory

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val userListRepository =
        ServiceLoader.provideUserListRepository(requireContext())
    val talkRepository=
        ServiceLoader.provideTalkRepository(requireContext())
    val addEditRepository=
        ServiceLoader.provideAddEditRepository(requireContext())
    return ViewModelFactory(addEditRepository,talkRepository,userListRepository)
}