package com.amebaownd.pikohan_nwiatori.noticemeplz.userList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginRight
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UserAndUsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.ItemUserBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.PxDpComverter
import java.lang.IllegalArgumentException

class UserListAdapter(private val viewModel: UserListViewModel,private val context: Context?) :
    ListAdapter<UserAndUsingService, UserListAdapter.ViewHolder>(UserListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent,context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel,item)
    }

    class ViewHolder(private val binding: ItemUserBinding,private val context:Context?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel:UserListViewModel,item:UserAndUsingService){
            binding.viewModel = viewModel
            binding.user = item.user
            if(context!=null) {
                item.usingService.sortedBy { it.service_code }.forEach {
                    val view = View(context).apply {
                        val layoutParams =  LinearLayout.LayoutParams(
                            PxDpComverter.dp2Px(16f, context).toInt(),
                            PxDpComverter.dp2Px(16f, context).toInt()
                        )
                        layoutParams.setMargins(0,0,PxDpComverter.dp2Px(4f,context).toInt(),0)
                        this.layoutParams=layoutParams

                        this.background = when (it.service_code) {
                            0x01 -> MyContext.getDrawable(R.drawable.icon_green_service)
                            0x02 -> MyContext.getDrawable(R.drawable.icon_sms_service)
                            0x04 -> MyContext.getDrawable(R.drawable.icon_slack_service)
                            else ->
                                throw IllegalArgumentException("There is not such serviceCode ${it.service_code}")
                        }
                    }
                    binding.userItemServiceLayout.addView(view)
                }
            }
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent:ViewGroup,context:Context?):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding,context)
            }
        }
    }
}

class UserListDiffCallback : DiffUtil.ItemCallback<UserAndUsingService>() {
    override fun areItemsTheSame(
        oldItem: UserAndUsingService,
        newItem: UserAndUsingService
    ): Boolean {
        return oldItem.user.id == newItem.user.id
    }

    override fun areContentsTheSame(
        oldItem: UserAndUsingService,
        newItem: UserAndUsingService
    ): Boolean {
        return oldItem.equals(newItem)
    }

}