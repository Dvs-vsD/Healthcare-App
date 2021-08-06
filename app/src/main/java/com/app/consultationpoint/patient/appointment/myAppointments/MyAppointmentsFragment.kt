package com.app.consultationpoint.patient.appointment.myAppointments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.databinding.FragmentMyAppointmentsBinding
import com.app.consultationpoint.patient.appointment.adapter.MonthlyAptAdapter
import com.app.consultationpoint.patient.doctor.DoctorListFragment
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyAppointmentsFragment : BaseFragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMyAppointmentsBinding
    private var adapter: MonthlyAptAdapter? = null
    private val viewModel by viewModel<MyAptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            viewModel.getMonthlyAptList().observe(this, {
                if (adapter != null) {
                    adapter?.notifyDataSetChanged()
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

        viewModel.fetchAptFromFirebase()

        viewModel.initRepo()

        val monthlyApt = viewModel.getMonthlyAptList()

        adapter = MonthlyAptAdapter(viewModel.getMonthlyAptList(), this@MyAppointmentsFragment)

        if (monthlyApt.value?.isEmpty() == true) {
            binding.tvNoData.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = adapter
        }

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.fetchAptFromFirebase()
//            viewModel.initRepo()
            Handler().postDelayed({ binding.pullToRefresh.isRefreshing = false }, 3000)
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