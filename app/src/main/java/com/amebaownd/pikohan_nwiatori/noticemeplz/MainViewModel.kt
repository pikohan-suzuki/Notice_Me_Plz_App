package com.amebaownd.pikohan_nwiatori.noticemeplz

import androidx.lifecycle.ViewModel
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.User
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService

class MainViewModel : ViewModel(){

    var user: User?=null
    var name:String? =null
    var usingServices:List<UsingService>? = null

    fun clear(){
        user=null
        name=null
        usingServices=null
    }
}