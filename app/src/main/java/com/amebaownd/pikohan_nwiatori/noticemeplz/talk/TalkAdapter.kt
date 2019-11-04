package com.amebaownd.pikohan_nwiatori.noticemeplz.talk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.Constants
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.Message
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.ItemTalkBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.MyContext
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.PxDpConverter

class TalkAdapter(private val viewModel: TalkViewModel, private val context: Context?) :
    ListAdapter<Message, TalkAdapter.ViewHolder>(TalkDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent,context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    class ViewHolder(private val binding: ItemTalkBinding, private val context: Context?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: TalkViewModel, item: Message) {
            binding.viewModel = viewModel
            binding.message = item
            binding.executePendingBindings()
            val serviceCode = item.service_code
            binding.messageItemServiceLayout.removeAllViews()
            if (serviceCode.and(Constants.MAIL_SERVICE_CODE) != 0) {
                val view = View(context).apply {
                    background = MyContext.getDrawable(R.drawable.icon_sms_service)
                    val layoutParams = LinearLayout.LayoutParams(
                        PxDpConverter.dp2Px(8f, context).toInt(),
                        PxDpConverter.dp2Px(8f, context).toInt()
                    )
                    layoutParams.setMargins(0, 0, PxDpConverter.dp2Px(4f, context).toInt(), 0)
                    this.layoutParams=layoutParams
                }
                binding.messageItemServiceLayout.addView(view)
            }
            if(context!=null) {
                if (serviceCode.and(Constants.SLACK_SERVICE_CODE) != 0) {
                    val view = View(context).apply {
                        background = MyContext.getDrawable(R.drawable.icon_slack_service)
                        val layoutParams = LinearLayout.LayoutParams(
                            PxDpConverter.dp2Px(8f, context).toInt(),
                            PxDpConverter.dp2Px(8f, context).toInt()
                        )
                        layoutParams.setMargins(0, 0, PxDpConverter.dp2Px(4f, context).toInt(), 0)
                        this.layoutParams=layoutParams
                    }
                    binding.messageItemServiceLayout.addView(view)
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup,context:Context?): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTalkBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding,context)
            }
        }
    }
}

class TalkDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(
        oldItem: Message,
        newItem: Message
    ): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(
        oldItem: Message,
        newItem: Message
    ): Boolean {
        return oldItem == newItem
    }

}