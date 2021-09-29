package com.app.consultationpoint.patient.doctor

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.databinding.ActivityDoctorDetailsBinding
import com.app.consultationpoint.patient.appointment.bookAppointment.ChooseTimeActivity
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import io.realm.RealmList
import timber.log.Timber

@AndroidEntryPoint
class DoctorDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailsBinding
    private val viewModel by viewModels<DoctorViewModel>()
    private var room: RoomModel? = null
    private var docId: Long = 0
    private var userId: Long = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it.isNotEmpty() && it == "Room Created Successfully" && room != null) {
                goToChatScreen(room!!.room_id)
            } else if (it.startsWith("Failed to create chat room")) {
                this@DoctorDetailsActivity.showToast("Something went wrong!!! Try again")
            }

            if (it.isNotEmpty() && it.startsWith("count")) {
                val count = it.substring(5, it.length)
                Timber.d("FirebaseSource $count")
                if (count.isNotEmpty() && count != "0")
                    binding.tvPatientCount.text = "$count +"
                else
                    binding.tvPatientCount.text = "--"
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
            binding.ivProfile.loadImageFromCloud(model.profile!!)
        }
        if (model.specialist_id != 0)
            binding.tvSpecAdr.text = viewModel.getSpecializationName(model.specialist_id)
        else
            binding.tvSpecAdr.text = ""

        if (model.experience_yr.isNotEmpty())
            binding.tvYearCount.text = model.experience_yr + " yrs+"
        else
            binding.tvYearCount.text = "--"

        viewModel.getPatientCount(docId)

        if (model.about_info != "") {
            binding.tvAbout.text = model.about_info
        } else {
            binding.tvAbout.text = "Not yet added"
        }

        if (model.mobile.isNotEmpty()) {
            binding.tvPhone.text = model.mobile
        } else {
            binding.cvCall.hide()
            binding.tvPhone.hide()
            binding.tvPhoneHint.hide()
        }
        if (model.email.isNotEmpty()) {
            binding.tvEmail.text = model.email
        } else {
            binding.tvEmail.hide()
            binding.tvEmailHint.hide()
        }

        val adrModel = viewModel.getAddress(docId)

        val adr = adrModel.address
        if (adr != "") {
            binding.tvAddress.text = adr
        } else {
            binding.tvAddress.hide()
            binding.tvAddressHint.hide()
        }

        val city = adrModel.city
        if (city != "")
            binding.tvCity.text = city
        else {
            binding.tvCity.hide()
            binding.tvCityHint.hide()
        }

        val state = adrModel.state
        if (state != "")
            binding.tvState.text = state
        else {
            binding.tvState.hide()
            binding.tvStateHint.hide()
        }

        val country = adrModel.country
        if (country != "")
            binding.tvCountry.text = country
        else {
            binding.tvCountry.hide()
            binding.tvCountryHint.hide()
        }

        val pinCode = adrModel.pincode
        if (pinCode != 0)
            binding.tvPinCode.text = pinCode.toString()
        else {
            binding.tvPinCode.hide()
            binding.tvPinCodeHint.hide()
        }

        binding.cvCall.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${model.mobile}")))
        }

        binding.btnBookAppt.setOnClickListener {
            val intent = Intent(this, ChooseTimeActivity::class.java)
            intent.putExtra("doctor_id", docId)
            intent.putExtra("isUpdate", false)
            startActivity(intent)
        }

        binding.cvChat.setOnClickListener {
            Utils.showProgressDialog(this)
            userId = Utils.getUserId().toLong()
            val roomId = viewModel.checkRoomAvailability(userId, docId)
            if (roomId != 0L) {
                Utils.dismissProgressDialog()
                Timber.d("From Realm")
                goToChatScreen(roomId)
            } else {
                createRoom(docId, docName, userId)
            }
        }
    }

    private fun goToChatScreen(room_id: Long) {
        val intent = Intent(this, ChatScreenActivity::class.java)
        intent.putExtra("receiver_id", docId)
        intent.putExtra("room_id", room_id)
        startActivity(intent)
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
        sender.room_id = room?.room_id ?: 0
        sender.user_id = userId
        sender.added_by_id = userId
        sender.updated_at = System.currentTimeMillis()
        sender.is_deleted = false

        Thread.sleep(1)

        val receiver = ParticipantModel()
        receiver.paticipant_id = System.currentTimeMillis()
        receiver.room_id = room?.room_id ?: 0
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
            viewModel.createChatRoom(room!!)
        }
    }
}