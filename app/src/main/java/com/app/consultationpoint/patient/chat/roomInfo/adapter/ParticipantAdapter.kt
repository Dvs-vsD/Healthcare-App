package com.app.consultationpoint.patient.chat.roomInfo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfParticipantListBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.loadImage
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.bumptech.glide.Glide

class ParticipantAdapter(
    private val list: LiveData<ArrayList<UserModel>>,
    private val context: Context
) :
    RecyclerView.Adapter<ParticipantAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: RowOfParticipantListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: UserModel) {
            if (item.profile != null && item.profile!!.isNotEmpty()) {
                binding.ivProfile.loadImageFromCloud(item.profile!!)
            }
            if (item.id.toString() != Utils.getUserId())
                binding.tvParticipant.text = item.first_name + " " + item.last_name
            else
                binding.tvParticipant.text = "Me"
            binding.tvUserType.text = when (item.user_type_id) {
                0 -> "Patient"
                1 -> "Doctor"
                2 -> "Staff"
                3 -> "Laboratory"
                else -> ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            RowOfParticipantListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list.value?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return list.value?.size ?: 0
    }
}