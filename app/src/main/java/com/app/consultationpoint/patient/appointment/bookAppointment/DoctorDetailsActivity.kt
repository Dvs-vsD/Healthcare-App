package com.app.consultationpoint.patient.appointment.bookAppointment

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityDoctorDetailsBinding
import com.bumptech.glide.Glide
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DoctorDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailsBinding
    private val viewModel by viewModel<BookAptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inThis()
    }

    @SuppressLint("SetTextI18n")
    private fun inThis() {
        val docId = intent.getLongExtra("doctor_id",0)
        Timber.d("user Id %s", docId)
        val model = viewModel.getDoctorDetails(docId)

        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.tvDocName.text = model.first_name + " " + model.last_name
        binding.tvSpecAdr.text = model.specialization + ", " + model.city
        if (model.profile != "" && model.profile != "null") {
            Glide.with(this).load(model.profile).into(binding.ivProfile)
        }

        binding.btnBookAppt.setOnClickListener {
            val intent = Intent(this, ChooseTimeActivity::class.java)
            intent.putExtra("doctor_id", docId)
            startActivity(intent)
        }
    }
}