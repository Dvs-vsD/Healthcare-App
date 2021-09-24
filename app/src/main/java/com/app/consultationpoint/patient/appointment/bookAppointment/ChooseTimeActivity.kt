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
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import dagger.hilt.android.AndroidEntryPoint
import io.realm.RealmList
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChooseTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseTimeBinding
    private var doctor_id: Long? = 0
    private var userId: Long? = 0
    private var docName: String? = ""
    private var selectedTime: String? = ""
    private var selectedDate: String? = ""
    private var room: RoomModel? = null
    private val viewModel by viewModels<BookAptViewModel>()
    private val myAptViewModel by viewModels<MyAptViewModel>()
    private var apt_model: AppointmentModel? = null
    private val calender = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            if (it.isNotEmpty() && room != null && doctor_id != 0L) {
                createChatRoom(doctor_id ?: 0, docName ?: "", userId ?: 0)
            }
        })

        binding.ivBack.setOnClickListener { onBackPressed() }

        inThis()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun inThis() {

        apt_model = intent.getSerializableExtra("appointment_model") as AppointmentModel?

        doctor_id = apt_model?.doctor_id ?: intent.getLongExtra("doctor_id", 0)

        userId = Utils.getUserId().toLong()

        val today = calender.timeInMillis

        selectedDate = if (apt_model == null)
            SimpleDateFormat("yyyy-MM-dd").format(calender.time)
        else
            apt_model?.schedual_date

        if (apt_model != null) {
            selectedDate?.substring(0, 4)?.toInt()?.let { calender.set(Calendar.YEAR, it) }
            selectedDate?.substring(5, 7)?.toInt()?.let { calender.set(Calendar.MONTH, it - 1) }
            selectedDate?.substring(8, selectedDate?.length ?: 0)?.toInt()
                ?.let { calender.set(Calendar.DAY_OF_MONTH, it) }
            binding.calenderView.date = calender.timeInMillis

            if (calender.timeInMillis < today) {
                binding.calenderView.hide()
                binding.tvAptDetail.show()
                if (Utils.getUserType() == 0) {
                    val user = myAptViewModel.getDoctorDetails(apt_model!!.doctor_id)
                    binding.tvAptDetail.text =
                        "Doctor Name: ${user.first_name} ${user.last_name} \nDate: ${calender.time.formatTo("dd,MMMM yyyy")}"
                } else {
                    val user = myAptViewModel.getDoctorDetails(apt_model!!.patient_id)
                    binding.tvAptDetail.text =
                        "Patient Name: ${user.first_name} ${user.last_name} \nDate: ${calender.time.formatTo("dd, MMMM yyyy")}"
                }
                binding.etTitle.isFocusable = false
                binding.etTitle.isFocusableInTouchMode = false
                binding.etDisc.isFocusable = false
                binding.etDisc.isFocusableInTouchMode = false
//                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.btnBookAppt.hide()
            } else {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                listenClick()
                binding.calenderView.minDate = today
                binding.calenderView.show()
                binding.btnBookAppt.show()
                binding.tvAptDetail.hide()
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
                        model.created_by = apt_model!!.patient_id
                        model.appointment_id = apt_model!!.appointment_id
                        model.created_at = apt_model!!.created_at
                    } else {
                        model.doctor_id = doctor_id ?: 0
                        model.patient_id = userId ?: 0
                        model.created_by = userId ?: 0
                        model.appointment_id = System.currentTimeMillis()
                        model.created_at = System.currentTimeMillis()
                    }
                    model.updated_at = System.currentTimeMillis()

                    Timber.d("date %s", model.schedual_date)

                    viewModel.bookAppointment(model)

                    Toast.makeText(this, "Your Appointment booked!!!", Toast.LENGTH_SHORT).show()

                    if (apt_model == null) {
                        val docDetails = viewModel.getDoctorDetails(doctor_id ?: 0)
                        docName = docDetails.first_name + " " + docDetails.last_name
                        createChatRoom(doctor_id ?: 0, docName ?: "", userId ?: 0)
                    }

                    myAptViewModel.fetchAptFromRealm()

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

    private fun createChatRoom(doctorId: Long, docName: String, userId: Long) {

        room = RoomModel()

        room?.room_id = System.currentTimeMillis()
        room?.room_type = 1
        room?.created_by_id = userId
        room?.name = docName

        val sender = ParticipantModel()
        sender.paticipant_id = System.currentTimeMillis()
        sender.room_id = room?.room_id ?: 0
        sender.user_id = userId
        sender.added_by_id = userId
        sender.updated_at = System.currentTimeMillis()
        sender.is_deleted = false

        Thread.sleep(1)

        val receiver = ParticipantModel()
        receiver.paticipant_id = System.currentTimeMillis()
        receiver.room_id = room?.room_id ?: 0
        receiver.user_id = doctorId
        receiver.added_by_id = userId
        receiver.updated_at = System.currentTimeMillis()
        receiver.is_deleted = false

        val participantIdList = RealmList<Long>()
        participantIdList.add(sender.user_id)
        participantIdList.add(receiver.user_id)

        room?.user_ids_participants = participantIdList

        val list = RealmList<ParticipantModel>()
        list.add(sender)
        list.add(receiver)

        room?.list_participants = list
        room?.updated_at = System.currentTimeMillis()
        room?.created_at = System.currentTimeMillis()
        room?.is_req_accept_block = 0
        room?.is_deleted = false

        if (room != null)
            viewModel.createChatRoom(room!!, userId, doctorId)
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