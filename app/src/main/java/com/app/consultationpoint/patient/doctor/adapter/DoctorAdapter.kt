package com.app.consultationpoint.patient.doctor.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.RowOfDoctorListBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.doctor.DoctorDetailsActivity
import com.bumptech.glide.Glide
import timber.log.Timber

class DoctorAdapter(
    private val list: LiveData<ArrayList<UserModel>>,
    private var activity: FragmentActivity?
) : RecyclerView.Adapter<DoctorAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: RowOfDoctorListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "BinaryOperationInTimber")
        fun bind(item: UserModel) {
            if (item.profile != "" && item.profile != "null") {
                activity?.let { Glide.with(it).load(item.profile).into(binding.ivProfile) }
            }
            Timber.d(item.first_name + " " + item.doc_id)
            binding.tvDocName.text = "${item.first_name} ${item.last_name}"
//            binding.tvSpecAdr.text = "${item.specialization}, ${item.city}"
            binding.btnTakeAppointment.setOnClickListener {
                val intent = Intent(activity, ChooseTimeActivity::class.java)
                intent.putExtra("doctor_id", item.doc_id)
                activity?.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            RowOfDoctorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list.value?.get(position)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DoctorDetailsActivity::class.java)
            Timber.d("user id %s", list.value?.get(position)?.id)
            intent.putExtra("doctor_id", list.value?.get(position)?.id)
            activity?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.value?.size ?: 0
    }
}