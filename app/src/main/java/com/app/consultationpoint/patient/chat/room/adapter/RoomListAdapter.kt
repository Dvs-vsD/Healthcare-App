package com.app.consultationpoint.patient.chat.room.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfChatListBinding
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
import com.app.consultationpoint.patient.chat.room.RoomViewModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.loadImage
import java.util.*

class RoomListAdapter(
    private var list: ArrayList<RoomModel?>,
    private val context: FragmentActivity,
    private val viewModel: RoomViewModel
) :
    RecyclerView.Adapter<RoomListAdapter.MyViewHolder>() {

    fun setList(it: ArrayList<RoomModel?>) {
        list = it
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: RowOfChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RoomModel) {
            var name = ""
            val pArray: List<Long> = item.user_ids_participants

            for ((index, user) in pArray.withIndex()) {
                if (user.toString() != Utils.getUserId()) {
                    val userDetails = viewModel.getUserDetails(user)
                    if (pArray.size <= 2)
                        name = userDetails.first_name + " " + userDetails.last_name
                    else {
                        name += if (index == pArray.size - 1)
                            userDetails.first_name + " " + userDetails.last_name
                        else
                            userDetails.first_name + " " + userDetails.last_name + ", "
                    }
                }
            }

            val profile = item.photo
            if (profile != null && profile.isNotEmpty()) {
                binding.ivProfile.loadImage(profile)
            }
            binding.tvDocName.text = name

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
        list[position]?.let { holder.bind(it) }

        var doctorId = 0L
        val userId: Long = Utils.getUserId().toLong()
        if (list[position]?.list_participants != null) {
            for (participant in list[position]?.list_participants!!) {
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
            intent.putExtra("room_id", list[position]?.room_id)
            intent.putExtra("doctor_id", doctorId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}