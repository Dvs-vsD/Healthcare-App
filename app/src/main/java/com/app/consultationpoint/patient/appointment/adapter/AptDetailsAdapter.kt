package com.app.consultationpoint.patient.appointment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfAppointmentDetailBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.toDate

class AptDetailsAdapter(
    private val aptList: ArrayList<AppointmentModel>?,
    private val context: Context,
    private val viewModel: MyAptViewModel
) : RecyclerView.Adapter<AptDetailsAdapter.MyViewHolder>() {

//    private val viewModel = context.let { ViewModelProvider(it).get(MyAptViewModel::class.java) }

    inner class MyViewHolder(private val binding: RowOfAppointmentDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: AppointmentModel) {
            binding.tvDate.text = item.schedual_date.substring(8)
            binding.tvMonth.text =
                item.schedual_date.toDate("yyyy-MM-dd")?.formatTo("MMMM")
                    ?.substring(0, 3)?.uppercase()
            val userDetails: UserModel = if (Utils.getUserType() == 0)
                viewModel.getDoctorDetails(item.doctor_id)
            else
                viewModel.getDoctorDetails(item.patient_id)

            binding.tvDocName.text = "${userDetails.first_name} ${userDetails.last_name}"
//            binding.tvSpecAdr.text = "${userDetails.specialization}, ${docDetails.city}"
            binding.tvTitleTime.text = "For ${item.title} at ${item.schedual_time}"

            binding.clRowAppointment.setOnClickListener {
                val intent = Intent(context, ChooseTimeActivity::class.java)
                intent.putExtra("appointment_model", item)
                intent.putExtra("isUpdate", true)
                context.startActivity(intent)
            }
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