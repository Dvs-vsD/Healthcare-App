package com.app.consultationpoint.patient.doctor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.FragmentDoctorListBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.ChatScreenActivity
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.patient.doctor.adapter.DoctorAdapter
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import io.realm.RealmList
import timber.log.Timber

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class DoctorListFragment : BaseFragment(), DoctorAdapter.OnButtonChatCLick {
    private var param1: String? = null
    private lateinit var binding: FragmentDoctorListBinding
    private var adapterDoctor: DoctorAdapter? = null
    private val viewModel: DoctorViewModel by viewModels()
    private var listUser: ArrayList<UserModel>? = null
    private var room: RoomModel? = null
    private var receiver_id: Long = 0
    private var searchString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

        viewModel.getStatus().observe(this, {
            Utils.dismissProgressDialog()

            if (it.startsWith("search"))
                viewModel.searchUser(searchString)

            if (it == "Doctor List Updated" && adapterDoctor != null) {
                Timber.d("Adapter notified by the init doctor list realm")
                Handler().post {
                    viewModel.fetchDocFromRDB()
                }
            }

            if (it.isNotEmpty() && it == "Room Created Successfully" && room != null) {
                goToChatScreen(room!!.room_id, receiver_id)
            } else if (it.startsWith("Failed to create chat room")) {
                activity?.showToast("Something went wrong!!! Try again")
            }
        })

        viewModel.getDoctorList().observe(this, {
            if (adapterDoctor != null) {
                listUser = it
                Timber.d("doctor list changed #Size : ${listUser?.size}")
                adapterDoctor?.setDataList(listUser)

                if (it.isNotEmpty())
                    binding.tvNoData.hide()
                else
                    binding.tvNoData.show()
            }

            if (binding.pullToRefresh.isRefreshing) {
                binding.pullToRefresh.isRefreshing = false
                searchString = ""
                binding.etSearch.setText("")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utils.getUserType() == 0)
            binding.etSearch.hint = getString(R.string.et_hint_search_doctor)
        else
            binding.etSearch.hint = getString(R.string.et_hint_search_patient)

        viewModel.fetchDocFromRDB()
        viewModel.fetchDocFromFB(1)

        if (listUser == null)
            listUser = ArrayList()

        listUser = viewModel.getDoctorList().value
        adapterDoctor = DoctorAdapter(listUser, requireContext(), viewModel, this@DoctorListFragment)

        binding.recyclerView.layoutManager = GridLayoutManager(activity, 2)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapterDoctor

        binding.etSearch.addTextChangedListener { text ->
            if (text?.trim().toString().isNotEmpty()) {
                searchString = text?.trim().toString()
                if (Utils.getUserType() == 1 && Utils.isNetworkAvailable(requireContext()))
                    viewModel.searchFromFB(searchString)
                else
                    viewModel.searchUser(searchString)
            } else {
                searchString = ""
                viewModel.searchUser("")
            }
        }

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.fetchDocFromFB(1)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            DoctorListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1.toString())
                }
            }
    }

    override fun onChatBtnClick(receiver_id: Long) {
        Utils.showProgressDialog(requireActivity())
        this.receiver_id = receiver_id
        createChatRoom(
            receiver_id,
            Utils.getFirstName() + " " + Utils.getLastName(),
            Utils.getUserId().toLong()
        )
    }

    private fun goToChatScreen(room_id: Long, receiver_id: Long) {
        val intent = Intent(activity, ChatScreenActivity::class.java)
        intent.putExtra("receiver_id", receiver_id)
        intent.putExtra("room_id", room_id)
        startActivity(intent)
    }

    private fun createChatRoom(receiver_id: Long, docName: String, sender_id: Long) {
        room = RoomModel()

        room?.room_id = System.currentTimeMillis()
        room?.room_type = 1
        room?.created_by_id = sender_id
        room?.name = docName

        val sender = ParticipantModel()
        sender.paticipant_id = System.currentTimeMillis()
        sender.room_id = room?.room_id ?: 0
        sender.user_id = sender_id
        sender.added_by_id = sender_id
        sender.updated_at = System.currentTimeMillis()
        sender.is_deleted = false

        Thread.sleep(1)

        val receiver = ParticipantModel()
        receiver.paticipant_id = System.currentTimeMillis()
        receiver.room_id = room?.room_id ?: 0
        receiver.user_id = receiver_id
        receiver.added_by_id = sender_id
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
            viewModel.createChatRoom(room!!)
    }
}