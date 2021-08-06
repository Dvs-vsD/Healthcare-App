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
import com.app.consultationpoint.patient.appointment.myAppointments.MyAptViewModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import io.realm.RealmList
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ChooseTimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseTimeBinding
    private var doctor_id: Long? = 0
    private var userId: Long? = 0
    private var docName: String? = ""
    private var selectedTime: String? = ""
    private var selectedDate: String? = ""
    private var room: RoomModel? = null
    private val viewModel by viewModel<BookAptViewModel>()
    private val myAptViewModel by viewModel<MyAptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            if (it.isNotEmpty() && room != null && doctor_id != 0L) {
                createChatRoom(doctor_id?:0, docName?:"", userId?:0)
            }
        })

        inThis()
    }

    @SuppressLint("SimpleDateFormat")
    private fun inThis() {

        doctor_id = intent.getLongExtra("doctor_id", 0)
        userId = Utils.getUserId().toLong()

        binding.ivBack.setOnClickListener { onBackPressed() }

        val calender = Calendar.getInstance()
        val today = calender.timeInMillis
        binding.calenderView.minDate = today

        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calender.time)

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
                    model.doctor_id = doctor_id ?: 0
                    model.patient_id = userId?:0
                    model.created_by = userId?:0
                    model.schedual_date = selectedDate.toString()
                    model.schedual_time = selectedTime ?: ""
                    model.title = title
                    model.note = desc

                    Timber.d("date %s", model.schedual_date)

                    viewModel.bookAppointment(model)

                    Toast.makeText(this, "Your Appointment booked!!!", Toast.LENGTH_SHORT).show()

                    val docDetails = viewModel.getDoctorDetails(doctor_id ?: 0)
                     docName = docDetails.first_name + " " + docDetails.last_name

                    createChatRoom(doctor_id ?: 0, docName?:"", userId?:0)

                    myAptViewModel.initRepo()

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