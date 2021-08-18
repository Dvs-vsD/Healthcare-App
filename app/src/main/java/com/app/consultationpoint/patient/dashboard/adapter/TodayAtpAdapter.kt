package com.app.consultationpoint.patient.dashboard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfTodayAptBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.toDate
import com.bumptech.glide.Glide

class TodayAtpAdapter(
    private val list: LiveData<ArrayList<AppointmentModel>>,
    private val context: Context,
    private val doctorDetails: LiveData<ArrayList<UserModel>>
) : RecyclerView.Adapter<TodayAtpAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: RowOfTodayAptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: AppointmentModel) {
            binding.tvDocName.text =
                doctorDetails.value?.get(adapterPosition)?.first_name + " " + doctorDetails.value?.get(
                    adapterPosition
                )?.last_name
//            binding.tvSpecAdr.text = doctorDetails.specialization + ", " + doctorDetails.city
            if (doctorDetails.value?.get(adapterPosition)?.profile != "" && doctorDetails.value?.get(
                    adapterPosition
                )?.profile != "null"
            ) {
                Glide.with(context).load(doctorDetails.value?.get(adapterPosition)?.profile)
                    .into(binding.ivProfile)
            }
            binding.tvAvailableText.text =
                "Appointment For " + item.title + "\n" + "on " + item.schedual_date.toDate("yyyy-MM-dd")
                    ?.formatTo(
                        "dd/MM/yyyy"
                    ) + " at " + item.schedual_time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowOfTodayAptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list.value?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return list.value?.size ?: 0
    }
}