package com.amebaownd.pikohan_nwiatori.noticemeplz.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amebaownd.pikohan_nwiatori.noticemeplz.addEditUser.AddEditUserAdapter
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.talk.TalkAdapter
import com.amebaownd.pikohan_nwiatori.noticemeplz.userList.UserListAdapter

@BindingAdapter("app:add_edit_items")
fun setAddEditItems(listView:RecyclerView,items:List<UsingService>?){
    if(items!=null)
        (listView.adapter as AddEditUserAdapter).submitList(items)
}

@BindingAdapter("app:user_list_items")
fun setUserListItems(listView:RecyclerView,items:List<UserAndUsingService>?){
    if(items!=null)
        (listView.adapter as UserListAdapter).submitList(items)
}

@BindingAdapter("app:talk_items")
fun setTalkItems(listView:RecyclerView,items:List<Message>?){
    if(items!=null)
        (listView.adapter as TalkAdapter).submitList(items)
}