package com.app.consultationpoint.patient.chat.room.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.RowOfChatListBinding
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
import com.app.consultationpoint.patient.chat.room.RoomViewModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.show
import java.util.*

class RoomListAdapter(
    private var list: ArrayList<RoomModel>,
    private val context: FragmentActivity,
    private val viewModel: RoomViewModel
) :
    RecyclerView.Adapter<RoomListAdapter.MyViewHolder>() {

    fun setList(it: ArrayList<RoomModel>) {
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
                    if (pArray.size <= 2) {
                        if (userDetails.profile != null && userDetails.profile != "")
                            binding.ivProfile.loadImageFromCloud(userDetails.profile!!)
                        else
                            binding.ivProfile.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.default_user
                                )
                            )
                        name = userDetails.first_name + " " + userDetails.last_name
                    } else {
                        val profile = item.photo
                        if (profile != null && profile.isNotEmpty())
                            binding.ivProfile.loadImageFromCloud(profile)
                        else
                            binding.ivProfile.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.default_user
                                )
                            )

                        name += if (index == pArray.size - 1)
                            userDetails.first_name + " " + userDetails.last_name
                        else
                            userDetails.first_name + " " + userDetails.last_name + ", "
                    }
                }
            }

            binding.tvDocName.text = name

            if (item.last_message != null) {
                val lastMsg = item.last_message
                binding.tvLastMsg.show()
                binding.tvLastMsg.text = lastMsg?.content
                val time = lastMsg?.let { Date(it.created_at) }
                binding.tvLastMsgTime.text = time?.formatTo("h:mm a")
            } else {
                val time = Date(item.created_at)
                binding.tvLastMsg.hide()
                binding.tvLastMsgTime.text = time.formatTo("h:mm a")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowOfChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])

        var doctorId = 0L
        val userId: Long = Utils.getUserId().toLong()
        if (list[position].list_participants != null) {
            for (participant in list[position].list_participants) {
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
            intent.putExtra("room_id", list[position].room_id)
            intent.putExtra("receiver_id", doctorId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}