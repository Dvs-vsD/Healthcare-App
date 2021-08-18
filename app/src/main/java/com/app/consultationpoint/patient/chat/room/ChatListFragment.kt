package com.app.consultationpoint.patient.chat.room

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.databinding.FragmentChatListBinding
import com.app.consultationpoint.patient.chat.room.adapter.RoomListAdapter
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val ARG_PARAM1 = "param1"

class ChatListFragment : BaseFragment() {
    private var param1: Int? = null
    private var userId: Long = 0
    private lateinit var binding: FragmentChatListBinding
    private var adapter: RoomListAdapter? = null
    private val viewModel by viewModel<RoomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1).toString().toInt()
        }

        viewModel.getRoomList().observe(this, {
            if (it != null && it.isNotEmpty() && adapter != null) {
                adapter?.setList(it)
                binding.tvNoData.visibility = View.GONE
                Timber.d("Room adapter notified %s", it)
            }
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

        viewModel.fetchRoomsFromFirebase(userId)

        viewModel.roomsFromRealm(userId)

        binding.recyclerView.setHasFixedSize(true)

        val roomList: LiveData<ArrayList<RoomModel?>> = viewModel.getRoomList()

        adapter = activity?.let { context ->
            roomList.value?.let { list ->
                RoomListAdapter(list, context)
            }
        }

        if (roomList.value?.isEmpty() == true) {
            binding.tvNoData.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.GONE
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
            Handler(Looper.getMainLooper()).postDelayed({
                binding.pullToRefresh.isRefreshing = false
            }, 3000)
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