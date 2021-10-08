package com.app.consultationpoint.patient.chat.chatScreen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfOthersMsgBinding
import com.app.consultationpoint.databinding.RowOfSelfMsgBinding
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenViewModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(
    private var chatList: ArrayList<MessageModel>,
    private var viewModel: ChatScreenViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val userId = Utils.getUserId().toLong()
    private val self = 100
    private val other = 200

    fun setData(it: ArrayList<MessageModel>) {
        chatList.clear()
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
        if (chatList.get(position).sender_id == userId)
            (holder as SelfViewHolder).bind(chatList[position])
//            chatList.get(position).let { (holder as SelfViewHolder).bind(it) }
        else
            (holder as OtherViewHolder).bind(chatList[position])
//            chatList.get(position).let { (holder as OtherViewHolder).bind(it) }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender_id == userId)
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
            val profile = Utils.getUserProfile()
            if (profile.isNotEmpty())
                binding.ivProfile.loadImageFromCloud(profile)
        }
    }

    inner class OtherViewHolder(private val binding: RowOfOthersMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageModel) {
            binding.tvMsg.text = item.content
            val date = Date(item.created_at)
            binding.tvTime.text = date.formatTo("h:mm a")
            val userDetails = viewModel.getDoctorDetails(item.sender_id)
            if (userDetails.profile != null && userDetails.profile!!.isNotEmpty()) {
                binding.ivProfile.loadImageFromCloud(userDetails.profile!!)
            }
        }
    }
}