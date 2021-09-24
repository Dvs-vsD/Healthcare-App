package com.app.consultationpoint.patient.appointment.myAppointments

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.viewModels
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.databinding.FragmentMyAppointmentsBinding
import com.app.consultationpoint.patient.appointment.adapter.MonthlyAptAdapter
import com.app.consultationpoint.patient.doctor.DoctorListFragment
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class MyAppointmentsFragment : BaseFragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMyAppointmentsBinding
    private var adapter: MonthlyAptAdapter? = null
    private val viewModel by viewModels<MyAptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

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
                        viewModel.fetchAptFromRealm()
                    }
                }
            })
        }
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

        if (Utils.getUserType() == 0)
            binding.fab.show()
        else
            binding.fab.hide()

        viewModel.fetchAptFromFirebase()

        viewModel.fetchAptFromRealm()

        val monthlyApt = viewModel.getMonthlyAptList().value

        adapter = monthlyApt?.let { list ->
            MonthlyAptAdapter(
                list,
                requireContext(),
                viewModel
            )
        }

        if (monthlyApt?.isEmpty() == true) {
            binding.tvNoData.show()
        } else {
            binding.tvNoData.hide()
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.fetchAptFromFirebase()
        }

        binding.fab.setOnClickListener {
            mFragmentNavigation.pushFragment(DoctorListFragment.newInstance(0))
        }
    }

    override fun onResume() {
        viewModel.fetchAptFromRealm()
        super.onResume()
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