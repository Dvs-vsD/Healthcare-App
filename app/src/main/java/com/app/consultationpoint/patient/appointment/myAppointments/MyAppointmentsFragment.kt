package com.app.consultationpoint.patient.appointment.myAppointments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.databinding.FragmentMyAppointmentsBinding
import com.app.consultationpoint.patient.appointment.adapter.MonthlyAptAdapter
import com.app.consultationpoint.patient.doctor.DoctorListFragment
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.bind
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "specificUser"
private const val ARG_PARAM3 = "spUserId"

@AndroidEntryPoint
class MyAppointmentsFragment : BaseFragment() {
    private var param1: String? = null
    private var specificUser: Boolean = false
    private var spUserId: Long? = null
    private lateinit var binding: FragmentMyAppointmentsBinding
    private var adapter: MonthlyAptAdapter? = null
    private val viewModel by viewModels<MyAptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            specificUser = it.getBoolean(ARG_PARAM2)
            spUserId = it.getLong(ARG_PARAM3)
        }

        viewModel.getMonthlyAptList().observe(this, { list ->

            if (adapter != null) {
                Timber.d("Apt list notified")
                adapter?.setList(list)
                if (list.isNotEmpty()) {
                    binding.tvNoData.hide()
                } else {
                    binding.tvNoData.show()
                }
            }

            if (binding.pullToRefresh.isRefreshing)
                binding.pullToRefresh.isRefreshing = false
        })

        viewModel.getStatus().observe(this, { status ->
            if (status == "My Apt Updated") {
                Handler().post {
                    if (specificUser)
                        viewModel.fYourAptWithSpecificUser(spUserId ?: 0)
                    else
                        viewModel.fetchAptFromRealm()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("Specific User $specificUser and id $spUserId")

        viewModel.fetchAptFromFirebase()

        if (specificUser) {
            binding.fab.hide()
            viewModel.fYourAptWithSpecificUser(spUserId ?: 0)
        } else {
            binding.fab.show()
            Timber.d("check: My Appointments first")
            viewModel.fetchAptFromRealm()
        }

        val monthlyApt = viewModel.getMonthlyAptList().value ?: ArrayList()

        adapter = MonthlyAptAdapter(monthlyApt, requireContext(), viewModel)

        if (monthlyApt.isEmpty()) {
            binding.tvNoData.show()
        } else {
            binding.tvNoData.hide()
        }

        binding.recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.fetchAptFromFirebase()
        }

        binding.fab.setOnClickListener {
            mFragmentNavigation.pushFragment(DoctorListFragment.newInstance(0))
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: Int) =
            MyAppointmentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1.toString())
                }
            }
    }
}