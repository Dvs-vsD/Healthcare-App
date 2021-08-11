package com.app.consultationpoint.patient.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.R
import com.app.consultationpoint.calender.CalenderFragment
import com.app.consultationpoint.databinding.FragmentDashboardBinding
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.patient.dashboard.adapter.TodayAtpAdapter
import com.app.consultationpoint.patient.doctor.DoctorListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.header_layout.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val ARG_PARAM1 = "param1"

class DashboardFragment : BaseFragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentDashboardBinding
    private val viewModel by viewModel<DashboardViewModel>()
    private var adapterTodayApt: TodayAtpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

        viewModel.getTodayAptList().observe(this, {
            if (it.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.tvNoData.visibility = View.GONE
            }
            if (adapterTodayApt != null && it.isNotEmpty()) {
                Timber.d("notified")
                adapterTodayApt?.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSearch.setOnClickListener {
            mFragmentNavigation.pushFragment(DoctorListFragment.newInstance(1))
        }

        val fragmentCalender = CalenderFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.llCalender, fragmentCalender)
        transaction.commit()

//        viewModel.init()

        adapterTodayApt = TodayAtpAdapter(viewModel.getTodayAptList(), this@DashboardFragment)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapterTodayApt
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1.toString())
                }
            }
    }
}