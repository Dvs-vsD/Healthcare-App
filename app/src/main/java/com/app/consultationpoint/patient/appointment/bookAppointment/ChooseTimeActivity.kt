package com.app.consultationpoint.patient.appointment.bookAppointment

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityChooseTimeBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_choose_time.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChooseTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseTimeBinding
    private var opp_user_id: Long? = 0
    private var myUserId: Long? = 0
    private var selectedTime: String? = ""
    private var selectedDate: String? = ""
    private val viewModel by viewModels<BookAptViewModel>()
    private val myAptViewModel by viewModels<MyAptViewModel>()
    private var apt_model: AppointmentModel? = null
    private val calender = Calendar.getInstance()
    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it.isNotEmpty() && it == "Appointment booked") {

                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK

                if (isUpdate)
                    this@ChooseTimeActivity.showToast("Your appointment updated successfully !!!")
                else
                    this@ChooseTimeActivity.showToast("Your appointment booked successfully !!!")

                startActivity(intent)
                finish()

            } else if (it.startsWith("Booking Failed")) {
                this@ChooseTimeActivity.showToast(it)
            }
        })

        binding.ivBack.setOnClickListener { onBackPressed() }

        inThis()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun inThis() {

        apt_model = intent.getSerializableExtra("appointment_model") as AppointmentModel?
        isUpdate = intent.getBooleanExtra("isUpdate", false)

        opp_user_id = if (apt_model != null) {
            if (Utils.getUserType() == 0)
                apt_model?.doctor_id
            else
                apt_model?.patient_id
        } else {
            intent.getLongExtra("user_id", 0)
        }

        Timber.d("My id $myUserId and Opposite Id $opp_user_id")

        myUserId = Utils.getUserId().toLong()

        val today = calender.timeInMillis

        selectedDate = if (apt_model == null)
            SimpleDateFormat("yyyy-MM-dd").format(calender.time)
        else
            apt_model?.schedual_date

        val docDetail: UserModel = myAptViewModel.getDoctorDetails(opp_user_id ?: 0)
        setNameProfile(docDetail)
        setSpecialization(docDetail)

        if (Utils.getUserType() == 0) {
            binding.tvUserType.text = "Doctor"
        } else {
            binding.tvUserType.text = "Patient"
        }

        if (apt_model != null) {
            selectedDate?.substring(0, 4)?.toInt()?.let { calender.set(Calendar.YEAR, it) }
            selectedDate?.substring(5, 7)?.toInt()?.let { calender.set(Calendar.MONTH, it - 1) }
            selectedDate?.substring(8, selectedDate?.length ?: 0)?.toInt()
                ?.let { calender.set(Calendar.DAY_OF_MONTH, it) }
            binding.calenderView.date = calender.timeInMillis

            val user: UserModel = if (Utils.getUserType() == 0) {
                myAptViewModel.getDoctorDetails(apt_model!!.doctor_id)
            } else {
                myAptViewModel.getDoctorDetails(apt_model!!.patient_id)
            }
            setNameProfile(user)
            setSpecialization(user)

            if (calender.timeInMillis < today) {
                binding.calenderView.hide()

                binding.tvDate.show()
                binding.tvDate.text = "on " + calender.time.formatTo("dd, MMMM yyyy")

                binding.etTitle.isFocusable = false
                binding.etTitle.isFocusableInTouchMode = false
                binding.etDisc.isFocusable = false
                binding.etDisc.isFocusableInTouchMode = false
                binding.btnBookAppt.hide()
            } else {
                listenClick()
                if (Utils.getUserType() == 0) {
                    setSpecialization(user)
                } else
                    binding.tvDate.hide()

                binding.calenderView.minDate = today
                binding.calenderView.show()
                binding.btnBookAppt.show()
            }

            binding.etTitle.setText(apt_model!!.title)
            binding.etDisc.setText(apt_model!!.note)
            binding.tvTimePicker.text = "Selected Time: ${apt_model!!.schedual_time}"
            binding.tvTimePicker.setTextColor(ContextCompat.getColor(this, R.color.black))

            selectedTime = apt_model!!.schedual_time
            binding.btnBookAppt.text = "Update Appointment"
        } else {
            binding.calenderView.minDate = today
            listenClick()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setNameProfile(user: UserModel) {
        if (user.profile != null && user.profile!!.isNotEmpty())
            binding.ivProfile.loadImageFromCloud(user.profile!!)

        binding.tvName.text =
            "${user.first_name} ${user.last_name}"
    }

    private fun setSpecialization(user: UserModel) {
        val specialization = viewModel.getSpecializationName(user.specialist_id)
        if (specialization.isNotEmpty()) {
            binding.tvDate.show()
            binding.tvDate.text = specialization
        } else {
            binding.tvDate.hide()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun listenClick() {
        binding.calenderView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calender.time)
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

                    model.schedual_date = selectedDate.toString()
                    model.schedual_time = selectedTime ?: ""
                    model.title = title
                    model.note = desc
                    if (apt_model != null) {
                        model.doctor_id = apt_model!!.doctor_id
                        model.patient_id = apt_model!!.patient_id
                        model.created_by = apt_model!!.created_by
                        model.appointment_id = apt_model!!.appointment_id
                        model.created_at = apt_model!!.created_at
                    } else {
                        if (Utils.getUserType() == 0) {
                            model.doctor_id = opp_user_id ?: 0
                            model.patient_id = myUserId ?: 0
                        } else {
                            model.doctor_id = myUserId ?: 0
                            model.patient_id = opp_user_id ?: 0
                        }
                        model.created_by = myUserId ?: 0
                        model.appointment_id = System.currentTimeMillis()
                        model.created_at = System.currentTimeMillis()
                    }
                    model.updated_at = System.currentTimeMillis()

                    Timber.d("date %s", model.schedual_date)

                    viewModel.bookAppointment(model)

                    Utils.showProgressDialog(this)

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
            tvTimePicker.setTextColor(ContextCompat.getColor(this, R.color.black))
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