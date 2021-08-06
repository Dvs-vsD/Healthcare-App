package com.app.consultationpoint.patient.chat.chatScreen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.app.consultationpoint.databinding.ActivityChatScreenBinding
import com.app.consultationpoint.patient.chat.chatScreen.adapter.ChatAdapter
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import com.app.consultationpoint.utils.Utils
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatScreenBinding
    private var roomId: Long = 0
    private var userId: Long = 0
    private var docId: Long = 0
    private val msgModel = MessageModel()
    private var chatAdapter: ChatAdapter? = null
    private val viewModel by viewModel<ChatScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStatus().observe(this, {
            if (it == "Sending Failed" && msgModel.message_id != 0L) {
                Timber.d("Msg Sent Again")
                viewModel.sendMsg(msgModel)
            }
        })

        viewModel.getMessages().observe(this, {
            if (it != null && it.isNotEmpty()) {
                chatAdapter?.notifyDataSetChanged()
            }
        })

        inThis()
    }

    @SuppressLint("SetTextI18n")
    private fun inThis() {
        roomId = intent.getLongExtra("room_id", 0)
        docId = intent.getLongExtra("doctor_id", 0)
        userId = Utils.getUserId().toLong()
        val docDetails = viewModel.getDoctorDetails(docId)
        binding.tvUserName.text = docDetails.first_name + " " + docDetails.last_name

        Timber.d("From tvUserName %s", binding.tvUserName.text.toString())
        Timber.d("From DB %s", docDetails.first_name + " " + docDetails.last_name)
        Timber.d("room id %s", roomId)

        val profile = docDetails.profile
        if (profile != null && profile != "" && profile != "null")
            Glide.with(this).load(profile).into(binding.ivProfile)

        binding.recyclerView.setHasFixedSize(true)

        viewModel.fetchMessages(roomId)

        getMessages()

        binding.ivBack.setOnClickListener { onBackPressed() }

//        binding.ivAttachment.setOnClickListener {
//            sendMessage("doctor side check")
//        }

        binding.btnSend.setOnClickListener {
            val content = binding.etMsg.text.trim().toString()
            if (content.isNotEmpty()) {
                sendMessage(content)
                binding.etMsg.setText("")
            }
        }
    }

    private fun getMessages() {
        val msgList: LiveData<ArrayList<MessageModel>> = viewModel.getMessages()
        chatAdapter = ChatAdapter(msgList)
        binding.recyclerView.adapter = chatAdapter
    }

    private fun sendMessage(content: String) {
        msgModel.message_id = System.currentTimeMillis()
        msgModel.room_id = roomId
        msgModel.sender_id = userId
        msgModel.content = content
        msgModel.content_url = ""
        msgModel.content_type = 0
        msgModel.status = 0
        msgModel.created_at = System.currentTimeMillis()
        msgModel.updated_at = System.currentTimeMillis()
        msgModel.is_deleted = false

        viewModel.sendMsg(msgModel)
    }
}