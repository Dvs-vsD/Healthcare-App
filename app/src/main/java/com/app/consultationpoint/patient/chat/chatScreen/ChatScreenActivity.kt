package com.app.consultationpoint.patient.chat.chatScreen

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityChatScreenBinding
import com.app.consultationpoint.patient.chat.chatScreen.adapter.ChatAdapter
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import com.app.consultationpoint.patient.chat.roomInfo.RoomInfoActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ChatScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatScreenBinding
    private var roomId: Long = 0
    private var userId: Long = 0
    private var docId: Long = 0
    private val msgModel = MessageModel()
    private var chatAdapter: ChatAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel by viewModels<ChatScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel.getStatus().observe(this, {
            if (it == "Sending Failed" && msgModel.message_id != 0L) {
                Timber.d("Msg Sent Again")
                viewModel.sendMsg(msgModel)
            }
        })

        viewModel.getMessages().observe(this, {
            if (it != null && it.isNotEmpty()) {
                chatAdapter?.setData(it)
                binding.recyclerView.scrollToPosition(it.size - 1)
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
        val userName = docDetails.first_name + " " + docDetails.last_name
        binding.tvUserName.text = userName

        Timber.d("From tvUserName %s", binding.tvUserName.text.toString())
        Timber.d("From DB %s", docDetails.first_name + " " + docDetails.last_name)
        Timber.d("room id %s", roomId)

        val profile = docDetails.profile
        if (profile != null && profile != "" && profile != "null") {
            binding.ivProfile.loadImage(profile)
        }

        binding.recyclerView.setHasFixedSize(true)

        viewModel.fetchMsgFromFB(roomId)

        viewModel.fetchMessages(roomId)

        getMessages()

        binding.ivBack.setOnClickListener { onBackPressed() }

//        binding.ivAttachment.setOnClickListener {
//            sendMessage("doctor side check")
//        }

        binding.tvUserName.setOnClickListener {
            val intent = Intent(this, RoomInfoActivity::class.java)
            intent.putExtra("room_id", roomId)
            intent.putExtra("userName", userName)
            val pairs = arrayOf(
                Pair<View, String>(binding.ivProfile, getString(R.string.imageTransition)),
                Pair<View, String>(binding.tvUserName, getString(R.string.nameTransition))
            )
            val options = ActivityOptions.makeSceneTransitionAnimation(this, *pairs)

            startActivity(intent, options.toBundle())
        }

        binding.btnSend.setOnClickListener {
            val content = binding.etMsg.text.trim().toString()
            if (content.isNotEmpty()) {
                sendMessage(content)
                binding.etMsg.setText("")
            }
        }
    }

    private fun getMessages() {
        linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = linearLayoutManager
        val msgList: LiveData<ArrayList<MessageModel>> = viewModel.getMessages()
        chatAdapter = ChatAdapter(msgList)
        binding.recyclerView.adapter = chatAdapter

        binding.recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom)
                linearLayoutManager?.smoothScrollToPosition(
                    binding.recyclerView, null, chatAdapter?.itemCount ?: 0
                )
        }
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