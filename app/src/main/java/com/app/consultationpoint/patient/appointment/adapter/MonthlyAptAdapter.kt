package com.app.consultationpoint.patient.appointment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfMonthlyAppointmentsBinding
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.patient.appointment.myAppointments.MyAppointmentsFragment
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.toDate

class MonthlyAptAdapter(
    private val list: LiveData<ArrayList<MonthlyAppointments>>,
    private val context: MyAppointmentsFragment
) : RecyclerView.Adapter<MonthlyAptAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: RowOfMonthlyAppointmentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: MonthlyAppointments) {
            binding.tvMonthYear.text = item.month.toDate("MM")?.formatTo("MMMM") + ", " + item.year
            binding.recyclerView.adapter = AptDetailsAdapter(item.appointment, context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowOfMonthlyAppointmentsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list.value?.get(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return list.value?.size ?: 0
    }
}