package com.app.consultationpoint.patient.dashboard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfTodayAptBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImage
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.toDate

class TodayAtpAdapter(
    private var list: ArrayList<AppointmentModel>?,
    private val context: Context,
    private val doctorDetails: LiveData<ArrayList<UserModel>>
) : RecyclerView.Adapter<TodayAtpAdapter.MyViewHolder>() {


    fun setList(it: ArrayList<AppointmentModel>?) {
        list = it
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: RowOfTodayAptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: AppointmentModel) {
            binding.tvDocName.text =
                doctorDetails.value?.get(adapterPosition)?.first_name + " " + doctorDetails.value?.get(
                    adapterPosition
                )?.last_name
//            binding.tvSpecAdr.text = doctorDetails.specialization + ", " + doctorDetails.city
            if (Utils.getUserType() == 0)
                binding.tvSpecAdr.show()
            else
                binding.tvSpecAdr.hide()

            if (doctorDetails.value?.get(adapterPosition)?.profile != "" && doctorDetails.value?.get(
                    adapterPosition
                )?.profile != "null"
            ) {
                doctorDetails.value?.get(adapterPosition)?.profile?.let {
                    if (it.isNotEmpty())
                        binding.ivProfile.loadImageFromCloud(it)
                }
            }
            binding.tvAvailableText.text =
                "Appointment For " + item.title + "\n" + "on " + item.schedual_date.toDate("yyyy-MM-dd")
                    ?.formatTo(
                        "dd/MM/yyyy"
                    ) + " at " + item.schedual_time

            binding.clDayApt.setOnClickListener {
                val intent = Intent(context, ChooseTimeActivity::class.java)
                intent.putExtra("appointment_model", item)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowOfTodayAptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}