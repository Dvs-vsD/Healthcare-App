package com.app.consultationpoint.patient.chat.room

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.databinding.FragmentChatListBinding
import com.app.consultationpoint.patient.chat.room.adapter.RoomListAdapter
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class ChatListFragment : BaseFragment() {
    private var param1: Int? = null
    private var userId: Long = 0
    private lateinit var binding: FragmentChatListBinding
    private var adapter: RoomListAdapter? = null
    private val viewModel by viewModels<RoomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1).toString().toInt()
        }

        viewModel.getRoomList().observe(this, {

            if (adapter != null) {
                adapter?.setList(it)

                if (it.isNotEmpty())
                    binding.tvNoData.hide()
                else
                    binding.tvNoData.show()

                Timber.d("Room adapter notified %s", it)
            }

            if (binding.pullToRefresh.isRefreshing)
                binding.pullToRefresh.isRefreshing = false
        })

        viewModel.getStatus().observe(this, {
            if (it == "Chat Room List Updated") {
                Handler().post {
                    viewModel.roomsFromRealm(userId)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = Utils.getUserId().toLong()

        /*if (Utils.getUserType() == 0)
            binding.etSearch.show()
        else*/
        binding.etSearch.hide()

        Timber.d("User Type ${Utils.getUserType()}")

        if (Utils.getUserType() == 0) {
            binding.etSearch.hint = "Search Doctor"
        } else {
            binding.etSearch.hint = "Search Patient"
        }

        viewModel.fetchRoomsFromFirebase(userId)

        viewModel.roomsFromRealm(userId)

        binding.recyclerView.setHasFixedSize(true)

        val roomList: LiveData<ArrayList<RoomModel?>> = viewModel.getRoomList()

        adapter = activity?.let { context ->
            roomList.value?.let { list ->
                RoomListAdapter(list, context, viewModel)
            }
        }

        if (roomList.value?.isEmpty() == true) {
            binding.tvNoData.show()
        } else {
            binding.tvNoData.hide()
        }

        binding.recyclerView.adapter = adapter

        binding.etSearch.addTextChangedListener { text ->
            if (text?.trim().toString().isNotEmpty()) {
                viewModel.searchDoctor(text.toString())
            } else {
                viewModel.searchDoctor("")
            }
        }

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.roomsFromRealm(userId)
        }
    }

    override fun onResume() {
        viewModel.roomsFromRealm(userId)
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1.toString())
                }
            }
    }
}