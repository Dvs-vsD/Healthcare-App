package com.app.consultationpoint.patient.chat.room.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfChatListBinding
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.bumptech.glide.Glide
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class RoomListAdapter(
    private val list: LiveData<ArrayList<RoomModel?>>,
    private val context: FragmentActivity
) :
    RecyclerView.Adapter<RoomListAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: RowOfChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RoomModel) {
            val profile = item.photo
            if (profile != null && profile.isNotEmpty()) {
                Glide.with(context).load(profile).into(binding.ivProfile)
            }
            binding.tvDocName.text = item.name

            if (item.last_message != null) {
                val lastMsg = item.last_message
                binding.tvLastMsg.visibility = View.VISIBLE
                binding.tvLastMsg.text = lastMsg?.content
            }
            val time = Date(item.created_at)
            binding.tvLastMsgTime.text = time.formatTo("HH:mm")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowOfChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list.value?.get(position)?.let { holder.bind(it) }

        var doctorId = 0L
        val userId: Long = Utils.getUserId().toLong()
        if (list.value?.get(position)?.list_participants != null) {
            for (participant in list.value?.get(position)?.list_participants!!) {
                if (participant.added_by_id != participant.user_id) {
                    doctorId = if (participant.user_id != userId)
                        participant.user_id
                    else
                        participant.added_by_id
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatScreenActivity::class.java)
            intent.putExtra("room_id", list.value?.get(position)?.room_id)
            intent.putExtra("doctor_id", doctorId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.value?.size?:0
    }
}