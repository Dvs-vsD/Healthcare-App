package com.app.consultationpoint.patient.doctor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.databinding.ActivityDoctorDetailsBinding
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.bumptech.glide.Glide
import io.realm.RealmList
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DoctorDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailsBinding
    private val viewModel by viewModel<DoctorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            if (it.isNotEmpty())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        inThis()
    }

    @SuppressLint("SetTextI18n")
    private fun inThis() {

        viewModel.fetchChatRooms()

        val docId = intent.getLongExtra("doctor_id", 0)
        Timber.d("user Id %s", docId)
        val model = viewModel.getDoctorDetails(docId)

        binding.ivBack.setOnClickListener { onBackPressed() }

        val docName = model.first_name + " " + model.last_name

        binding.tvDocName.text = docName
        binding.tvSpecAdr.text = model.specialization + ", " + model.city
        if (model.profile != "" && model.profile != "null") {
            Glide.with(this).load(model.profile).into(binding.ivProfile)
        }

        binding.btnBookAppt.setOnClickListener {
            val intent = Intent(this, ChooseTimeActivity::class.java)
            intent.putExtra("doctor_id", docId)
            startActivity(intent)
        }

        binding.cvChat.setOnClickListener {

            val userId = Utils.getUserId().toLong()
            val check = viewModel.checkRoomAvailability(userId, docId)

            if (check.isNotEmpty()) {
                Timber.d("From Realm")
                Toast.makeText(this, check, Toast.LENGTH_SHORT).show()
            } else {
                Timber.d("From Firebase")
                val room = RoomModel()
                room.room_id = System.currentTimeMillis()
                room.room_type = 1
                room.created_by_id = userId
                room.name = docName

                val sender = ParticipantModel()
                sender.paticipant_id = System.currentTimeMillis()
                sender.room_id = room.room_id
                sender.user_id = userId
                sender.added_by_id = userId
                sender.updated_at = System.currentTimeMillis()
                sender.is_deleted = false

                Thread.sleep(1)

                val receiver = ParticipantModel()
                receiver.paticipant_id = System.currentTimeMillis()
                receiver.room_id = room.room_id
                receiver.user_id = docId
                receiver.added_by_id = userId
                receiver.updated_at = System.currentTimeMillis()
                receiver.is_deleted = false

                val list = RealmList<ParticipantModel>()
                list.add(sender)
                list.add(receiver)

                room.list_participants = list
                room.updated_at = System.currentTimeMillis()
                room.created_at = System.currentTimeMillis()
                room.is_req_accept_block = 0
                room.is_deleted = false

                viewModel.createChatRoom(room, userId, docId)
            }
        }
    }
}