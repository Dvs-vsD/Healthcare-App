package com.app.consultationpoint.patient.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.consultationpoint.R
import com.app.consultationpoint.calender.CalenderFragment
import com.app.consultationpoint.databinding.FragmentDashboardBinding
import com.app.consultationpoint.patient.dashboard.adapter.TodayAtpAdapter
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import kotlinx.android.synthetic.main.header_layout.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDashboardBinding
    private val viewModel by viewModel<DashboardViewModel>()
    private var adapterTodayApt: TodayAtpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel.getTodayAptList().observe(this, {
            if (it.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.tvNoData.visibility = View.GONE
            }
            if (adapterTodayApt != null) {
                Timber.d("notified")
                adapterTodayApt?.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        addDoctorList()

        val fragmentCalender = CalenderFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.llCalender, fragmentCalender)
        transaction.commit()

        adapterTodayApt = TodayAtpAdapter(viewModel.getTodayAptList(), this@DashboardFragment)
        binding.recyclerView.adapter = adapterTodayApt
    }

//    private fun addDoctorList() {
//
//        val list: ArrayList<DoctorModel> = ArrayList()
//
//        val model = DoctorModel()
//
//        model.doc_id = "0"
//        model.first_name = "Susmita"
//        model.last_name = "Patel"
//        model.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_m5MvU6DXK6ax_7_fwh" +
//                    "CV5Y2IXfPlC0JUXIwl7JUwr9t6QChWPz3VqHOrn9bk0OOSfN8&usqp=CAU"
//        model.specialization = "Heart Specialist"
//        model.city = "Ahmedabad"
//        list.add(0, model)
//
//        val model1 = DoctorModel()
//
//        model1.doc_id = "1"
//        model1.first_name = "Mahi"
//        model1.last_name = "Tandel"
//        model1.profile =
//            "https://i.pinimg.com/originals/6d/fd/81/6dfd81a17e8a926a3f6eb34421879d3b.jpg"
//        model1.specialization = "Surgary"
//        model1.city = "Surat"
//        list.add(1, model1)
//
//        val model2 = DoctorModel()
//
//        model2.doc_id = "2"
//            model2.first_name = "Divyesh"
//        model2.last_name = "Patel"
//        model2.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjJlptqtznO7Xav19EM56_" +
//                    "P1gHavS53WgWHoPY1NjoM23l-oNpiFIsoy2RTa1-6YVmAlR3HYXXoZYPRg&usqp=CAU"
//        model2.specialization = "Lungs Specialist"
//        model2.city = "Navsari"
//        list.add(2, model2)
//
//        val model3 = DoctorModel()
//
//        model3.doc_id = "3"
//            model3.first_name = "Humayu"
//        model3.last_name = "Babar"
//        model3.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTi5Pb8zjPZiljxXA9Q1M" +
//                    "HSVyfj20Tn6P7Y8KM_vkveilH4yVlcOnZevOunD7NB3tReXq5OQ5mGbo8HmQ&usqp=CAU"
//        model3.specialization = "Eye Specialist"
//        model3.city = "Jamnagar"
//        list.add(3, model3)
//
//        val model4 = DoctorModel()
//
//        model4.doc_id = "4"
//            model4.first_name = "Disha"
//        model4.last_name = "Malhotra"
//        model4.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSOw5lJXy-eL907hbVwo9G" +
//                    "qWn-riJ5SA6_eyXC5G6ewJNah9yoLkxhsVp8U9Ly4UMvy6Fayzzb5Cs4f0g&usqp=CAU"
//        model4.specialization = "Nose Specialist"
//        model4.city = "Valsad"
//        list.add(4, model4)
//
//        val model5 = DoctorModel()
//
//        model5.doc_id = "5"
//            model5.first_name = "Jigar"
//        model5.last_name = "Pathak"
//        model5.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYfgu9u2rILGIoliql5b8C" +
//                    "ey7T1f1CG50lBKQ64fI2flqpldpK3Y6b5Imci7-YJ5NJLs96GkiimoyDqg&usqp=CAU"
//        model5.specialization = "Bone Specialist"
//        model5.city = "Surat"
//        list.add(5, model5)
//
//        val model6 = DoctorModel()
//
//        model6.doc_id = "6"
//            model6.first_name = "Ruchi"
//        model6.last_name = "Nayak"
//        model6.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTd25w6FlJXoQj_oTuY" +
//                    "8NnaypvXIvVHefbj8khTGujK-lKN3CY68puwl2IKQeUCesiSgLwce7i7UZotFQ&usqp=CAU"
//        model6.specialization = "Tounge Specialist"
//        model6.city = "Navsari"
//        list.add(6, model6)
//
//        val model7 = DoctorModel()
//
//        model7.doc_id = "7"
//            model7.first_name = "Prinkesh"
//        model7.last_name = "Patel"
//        model7.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTN5E27UMUabh1ttv9Dp9k3" +
//                    "Y_mrpF3vJaarg0cxF2q7EHHVnYAZz2ePakz6dqXSfUNb4twSfDoVS7UdnA&usqp=CAU"
//        model7.specialization = "Ear Specialist"
//        model7.city = "Ghandhinagar"
//        list.add(7, model7)
//
//        val model8 = DoctorModel()
//
//        model8.doc_id = "8"
//            model8.first_name = "Divya"
//        model8.last_name = "Patel"
//        model8.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSdO3dCh0tkJQw3DZV2fiL" +
//                    "5u7QtAmXdH-5JWGSzDtaAIpydPEyhi0bAf6PjmhrSLDuRQY4&usqp=CAU"
//        model8.specialization = "Brain Specialist"
//        model8.city = "Ghandhinagar"
//        list.add(8, model8)
//
//        val model9 = DoctorModel()
//
//        model9.doc_id = "9"
//            model9.first_name = "Divya"
//        model9.last_name = "Dave"
//        model9.profile =
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTd25w6FlJXoQj_oTuY" +
//                    "8NnaypvXIvVHefbj8khTGujK-lKN3CY68puwl2IKQeUCesiSgLwce7i7UZotFQ&usqp=CAU"
//        model9.specialization = "ENT Specialist"
//        model9.city = "Navsari"
//        list.add(9, model9)
//
//        viewModel.addDoctorList(list)
//    }

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