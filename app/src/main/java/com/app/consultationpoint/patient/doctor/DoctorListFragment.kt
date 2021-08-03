package com.app.consultationpoint.patient.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.databinding.FragmentDoctorListBinding
import com.app.consultationpoint.patient.doctor.adapter.DoctorAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val ARG_PARAM1 = "param1"

class DoctorListFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var binding: FragmentDoctorListBinding
    private var adapterDoctor: DoctorAdapter? = null
    private val viewModel by viewModel<DoctorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

        viewModel.getDoctorList().observe(this, {
            if (adapterDoctor != null) {
                adapterDoctor?.notifyDataSetChanged()
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

        if (param1 == "1")
            binding.grpToolbar.visibility = View.GONE
        else
            binding.grpToolbar.visibility = View.VISIBLE

        viewModel.init()

        Timber.d(viewModel.getDoctorList().value.toString())
        adapterDoctor = DoctorAdapter(viewModel.getDoctorList(), activity)

        binding.recyclerView.apply {
            this.layoutManager = GridLayoutManager(activity, 2)
            this.setHasFixedSize(true)
            this.adapter = adapterDoctor
        }

        binding.etSearch.addTextChangedListener { text ->
            if (text?.trim().toString().isNotEmpty()) {
                viewModel.searchDoctor(text.toString())
            } else {
                viewModel.searchDoctor("")
            }
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