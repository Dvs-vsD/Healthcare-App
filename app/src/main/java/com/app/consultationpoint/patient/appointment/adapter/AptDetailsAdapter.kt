package com.app.consultationpoint.patient.appointment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfAppointmentDetailBinding
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.myAppointments.MyAppointmentsFragment
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import com.app.consultationpoint.utils.Utils.formatTo

class AptDetailsAdapter(
    private val aptList: ArrayList<AppointmentModel>?,
    private val context: MyAppointmentsFragment
) : RecyclerView.Adapter<AptDetailsAdapter.MyViewHolder>() {

    private val viewModel = context.let { ViewModelProvider(it).get(MyAptViewModel::class.java) }

    inner class MyViewHolder(private val binding: RowOfAppointmentDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: AppointmentModel) {
            binding.tvDate.text = item.schedual_date?.formatTo("dd")
            binding.tvMonth.text = item.schedual_date?.formatTo("MMMM")
            val docDetails: DoctorModel = viewModel.getDoctorDetails(item.doc_id)
            binding.tvDocName.text = "${docDetails.first_name} ${docDetails.last_name}"
            binding.tvSpecAdr.text = "${docDetails.specialization}, ${docDetails.city}"
            binding.tvTitleTime.text = "For ${item.appointmentTitle} at ${item.schedual_time}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowOfAppointmentDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        aptList?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return aptList?.size ?: 0
    }
}