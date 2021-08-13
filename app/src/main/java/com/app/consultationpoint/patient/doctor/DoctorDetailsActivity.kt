package com.app.consultationpoint.patient.doctor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.databinding.ActivityDoctorDetailsBinding
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
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
    private var room: RoomModel? = null
    private var docId: Long = 0
    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            if (userId != 0L && it.isNotEmpty() && room != null) {
                viewModel.createChatRoom(room!!, userId, docId)
            }
        })

        inThis()
    }

    @SuppressLint("SetTextI18n")
    private fun inThis() {

        docId = intent.getLongExtra("doctor_id", 0)
        Timber.d("user Id %s", docId)
        val model = viewModel.getDoctorDetails(docId)

        binding.ivBack.setOnClickListener { onBackPressed() }

        val docName = model.first_name + " " + model.last_name

        binding.tvDocName.text = docName
//        binding.tvSpecAdr.text = model.specialization + ", " + model.city
        if (model.profile != "" && model.profile != null) {
            Glide.with(this).load(model.profile).into(binding.ivProfile)
        }
        binding.tvYearCount.text = model.experience_yr + "+"

        if (model.about_info != "") {
            binding.tvAbout.text = model.about_info
        } else {
            binding.tvAbout.text = "Not yet added"
        }


        binding.btnBookAppt.setOnClickListener {
            val intent = Intent(this, ChooseTimeActivity::class.java)
            intent.putExtra("doctor_id", docId)
            startActivity(intent)
        }

        binding.cvChat.setOnClickListener {

            userId = Utils.getUserId().toLong()
            val roomId = viewModel.checkRoomAvailability(userId, docId)

            val intent = Intent(this, ChatScreenActivity::class.java)
            intent.putExtra("doctor_id", docId)

            if (roomId != 0L) {
                Timber.d("From Realm")
                intent.putExtra("room_id", roomId)
                startActivity(intent)
            } else {
                createRoom(docId,docName,userId)
                intent.putExtra("room_id", room?.room_id)
                startActivity(intent)
            }
        }
    }

    private fun createRoom(docId: Long, docName: String, userId: Long) {

        Timber.d("From Firebase")
        room = RoomModel()
        room?.room_id = System.currentTimeMillis()
        room?.room_type = 1
        room?.created_by_id = userId
        room?.name = docName

        val sender = ParticipantModel()
        sender.paticipant_id = System.currentTimeMillis()
        sender.room_id = room?.room_id?:0
        sender.user_id = userId
        sender.added_by_id = userId
        sender.updated_at = System.currentTimeMillis()
        sender.is_deleted = false

        Thread.sleep(1)

        val receiver = ParticipantModel()
        receiver.paticipant_id = System.currentTimeMillis()
        receiver.room_id = room?.room_id?:0
        receiver.user_id = docId
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

        if (room != null) {
            viewModel.createChatRoom(room!!, userId, docId)
        }
    }
}