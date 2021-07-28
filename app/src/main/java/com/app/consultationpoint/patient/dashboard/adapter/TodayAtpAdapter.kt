package com.app.consultationpoint.patient.dashboard.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.RowOfTodayAptBinding
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.dashboard.DashboardFragment
import com.app.consultationpoint.patient.dashboard.DashboardViewModel
import com.app.consultationpoint.utils.Utils.formatTo
import com.bumptech.glide.Glide

class TodayAtpAdapter(
    private val list: LiveData<ArrayList<AppointmentModel>>,
    private val context: DashboardFragment
) : RecyclerView.Adapter<TodayAtpAdapter.MyViewHolder>() {

    private val viewModel = ViewModelProvider(context).get(DashboardViewModel::class.java)

    inner class MyViewHolder(private val binding: RowOfTodayAptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: AppointmentModel) {
            val doctorDetails = viewModel.getDoctorDetails(item.doc_id)
            binding.tvDocName.text = doctorDetails.first_name + " " + doctorDetails.last_name
            binding.tvSpecAdr.text = doctorDetails.specialization + ", " + doctorDetails.city
            if (doctorDetails.profile != "") {
                Glide.with(context).load(doctorDetails.profile).into(binding.ivProfile)
            }
            binding.tvAvailableText.text =
                "Appointment For " + item.appointmentTitle + "\n" + "on " + item.schedual_date?.formatTo(
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