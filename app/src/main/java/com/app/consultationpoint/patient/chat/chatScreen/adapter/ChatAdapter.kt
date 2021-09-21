package com.app.consultationpoint.patient.chat.chatScreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfOthersMsgBinding
import com.app.consultationpoint.databinding.RowOfSelfMsgBinding
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private var chatList: ArrayList<MessageModel>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val userId = Utils.getUserId().toLong()
    private val self = 100
    private val other = 200

    fun setData(it: ArrayList<MessageModel>) {
        chatList = it
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == self) {
            val view =
                RowOfSelfMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SelfViewHolder(view)
        } else {
            val view =
                RowOfOthersMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtherViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (chatList?.get(position)?.sender_id == userId)
            chatList?.get(position)?.let { (holder as SelfViewHolder).bind(it) }
        else
            chatList?.get(position)?.let { (holder as OtherViewHolder).bind(it) }
    }

    override fun getItemCount(): Int {
        return chatList?.size?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList?.get(position)?.sender_id == userId)
            self
        else
            other
    }

    inner class SelfViewHolder(private val binding: RowOfSelfMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageModel) {
            binding.tvMsg.text = item.content
            val date = Date(item.created_at)
            binding.tvTime.text = date.formatTo("h:mm a")
        }
    }

    inner class OtherViewHolder(private val binding: RowOfOthersMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageModel) {
            binding.tvMsg.text = item.content
            val date = Date(item.created_at)
            binding.tvTime.text = date.formatTo("h:mm a")
        }
    }
}