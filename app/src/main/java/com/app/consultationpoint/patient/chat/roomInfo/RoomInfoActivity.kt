package com.app.consultationpoint.patient.chat.roomInfo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.databinding.ActivityRoomInfoBinding
import com.app.consultationpoint.patient.chat.roomInfo.adapter.ParticipantAdapter
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.toDate
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@AndroidEntryPoint
class RoomInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomInfoBinding
    private var roomId: Long = 0
    private var userName: String = ""
    private var participantAdapter: ParticipantAdapter? = null
    private val viewModel by viewModels<RIViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getParticipantList().observe(this, {
            if (it != null && it.isNotEmpty() && participantAdapter != null) {
                participantAdapter?.notifyItemRangeInserted(0, it.size)
            }
        })

        inThis()
    }

    @SuppressLint("SetTextI18n")
    private fun inThis() {
        roomId = intent.getLongExtra("room_id", 0)
        val roomDetails = viewModel.getRoomDetails(roomId)

        val createdDt = Date(roomDetails.created_at)

        binding.tvDateOnCreated.text = createdDt.formatTo("dd, MMMM-yyyy")
        if (roomDetails.created_by_id.toString() == Utils.getUserId())
            binding.tvNameOfCreator.text = "You"
        else {
            val creatorDtl = viewModel.getCreatorDetails(roomDetails.created_by_id)
            binding.tvNameOfCreator.text = creatorDtl?.first_name + " " + creatorDtl?.last_name
        }
        userName = intent.getStringExtra("userName") ?: ""

        binding.tvUserName.text = userName

        binding.ivBack.setOnClickListener { onBackPressed() }

        viewModel.fetchParticipantFromR(roomId)
        participantAdapter = ParticipantAdapter(viewModel.getParticipantList(), this)
        binding.mRecyclerView.adapter = participantAdapter
    }
}