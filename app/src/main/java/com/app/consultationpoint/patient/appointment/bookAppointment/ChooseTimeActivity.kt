package com.app.consultationpoint.patient.appointment.bookAppointment

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.databinding.ActivityChooseTimeBinding
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.toDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ChooseTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseTimeBinding
    private var doc_id: String? = ""
    private var selectedTime: String? = ""
    private var selectedDate: String? = ""
    private val viewModel by viewModel<BookAptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inThis()
    }

    @SuppressLint("SimpleDateFormat")
    private fun inThis() {

        doc_id = intent.getStringExtra("doc_id")

        binding.ivBack.setOnClickListener { onBackPressed() }

        val calender = Calendar.getInstance()
        val today = calender.timeInMillis
        binding.calenderView.minDate = today

        selectedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calender.time)

        binding.calenderView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            selectedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calender.time)
            Timber.d(selectedDate)
        }

        binding.tvTimePicker.setOnClickListener { timePickerDialog(binding.tvTimePicker) }

        binding.btnBookAppt.setOnClickListener {
            val title = binding.etTitle.text.trim().toString()
            val desc = binding.etDisc.text.trim().toString()
            if (selectedTime?.isNotEmpty() == true) {
                if (title.isNotEmpty()) {
                    Timber.d("date %s", selectedDate)
                    Timber.d("time %s", selectedTime)
                    val model = AppointmentModel()
                    model.doc_id = doc_id ?: ""
                    model.patient_id = Utils.getUserId()
                    model.schedual_date = selectedDate?.toDate()
                    model.schedual_time = selectedTime ?: ""
                    model.appointmentTitle = title
                    model.appointmentDesc = desc

                    Timber.d("date %s", model.schedual_date.toString())

                    viewModel.bookAppointment(model)

                    Toast.makeText(this, "Your Appointment booked!!!", Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(this, BottomNavigationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Please Enter Appointment title", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please Select Time slot", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun timePickerDialog(tvTimePicker: TextView) {
        val cal = Calendar.getInstance()
        var timeSet: String
        val timePickerDialog = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minuteOfHour ->
            Timber.d("hour %s", hourOfDay.toString())
            timeSet = when {
                hourOfDay > 12 -> {
                    "PM"
                }
                hourOfDay == 0 -> {
                    "AM"
                }
                hourOfDay == 12 -> {
                    "PM"
                }
                else -> {
                    "AM"
                }
            }

            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minuteOfHour)
            selectedTime = SimpleDateFormat("hh:mm").format(cal.time) + " " + timeSet
            tvTimePicker.text = "Selected Time: $selectedTime"
        }

        TimePickerDialog(
            this,
            timePickerDialog,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }
}