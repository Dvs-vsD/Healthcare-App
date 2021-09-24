package com.app.consultationpoint.patient.doctor

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
import com.app.consultationpoint.patient.doctor.adapter.DoctorAdapter
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class DoctorListFragment : BaseFragment() {
    private var param1: String? = null
    private lateinit var binding: FragmentDoctorListBinding
    private var adapterDoctor: DoctorAdapter? = null
    private val viewModel: DoctorViewModel by viewModels()
    private var listUser: ArrayList<UserModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

        viewModel.getStatus().observe(this, {
            if (it == "Doctor List Updated" && adapterDoctor != null) {
                Timber.d("Adapter notified by the init doctor list realm")
                Handler().post {
                    viewModel.fetchDocFromRDB()
                }
            }
        })

        viewModel.getDoctorList().observe(this, {
            if (adapterDoctor != null) {
                listUser = it
                Timber.d("doctor list changed #Size : ${listUser?.size}")
//                adapterDoctor?.notifyItemRangeInserted(0,it.size)
                adapterDoctor?.setDataList(listUser)
//                adapterDoctor?.notifyDataSetChanged()

                if (it.isNotEmpty())
                    binding.tvNoData.hide()
                else
                    binding.tvNoData.show()
            }

            if (binding.pullToRefresh.isRefreshing)
                binding.pullToRefresh.isRefreshing = false
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
        adapterDoctor = DoctorAdapter(listUser, activity, viewModel)

        binding.recyclerView.layoutManager = GridLayoutManager(activity, 2)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = adapterDoctor

        binding.etSearch.addTextChangedListener { text ->
            if (text?.trim().toString().isNotEmpty()) {
                viewModel.searchDoctor(text.toString())
            } else {
                viewModel.searchDoctor("")
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
}