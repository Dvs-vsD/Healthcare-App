package com.app.consultationpoint.patient.dashboard.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.consultationpoint.databinding.SingleItemOfSpecialistBinding
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.bumptech.glide.Glide
import timber.log.Timber

class SpecialistAdapter(
    private var list: ArrayList<SpecialistModel>,
    private val context: FragmentActivity
) :
    RecyclerView.Adapter<SpecialistAdapter.MyViewHolder>() {

    fun setList(it: ArrayList<SpecialistModel>?) {
        if (it != null) {
            list = it
            notifyDataSetChanged()
        }
    }

    inner class MyViewHolder(private val binding: SingleItemOfSpecialistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SpecialistModel) {
            Glide.with(context).load(item.image).into(binding.ivSpecialistIcon)
            binding.ivSpecialistIcon.setColorFilter(Color.parseColor(item.color_code))
            binding.tvName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = SingleItemOfSpecialistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}