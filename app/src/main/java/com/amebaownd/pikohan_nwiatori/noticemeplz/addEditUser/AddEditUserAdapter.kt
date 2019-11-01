package com.amebaownd.pikohan_nwiatori.noticemeplz.addEditUser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.enum.ServiceCode
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.ItemServicesBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import java.lang.IllegalArgumentException

class AddEditUserAdapter(private val viewModel: AddEditUserViewModel) :
    ListAdapter<UsingService, AddEditUserAdapter.ViewHolder>(AddEditUserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel,item)
    }

    class ViewHolder(private val binding: ItemServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: AddEditUserViewModel, item:UsingService){
            binding.viewModel = viewModel
            binding.usingService= item
            binding.serviceName= when(item.service_code){
                0x01->"LINE"
                0x02->"SMS"
                0x04->"Slack"
                else->
                    throw IllegalArgumentException("There is not such serviceCode ${item.service_code}")
            }
            binding.serviceColor = when(item.service_code){
                0x01-> MyContext.getColor(R.color.colorLine)
                0x02-> MyContext.getColor(R.color.colorSMS)
                0x04-> MyContext.getColor(R.color.colorSlack)
                else->
                    throw IllegalArgumentException("There is not such serviceCode ${item.service_code}")
            }
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent:ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemServicesBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class AddEditUserDiffCallback : DiffUtil.ItemCallback<UsingService>() {
    override fun areItemsTheSame(
        oldItem: UsingService,
        newItem: UsingService
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: UsingService,
        newItem: UsingService
    ): Boolean {
        return oldItem.equals(newItem)
    }

}