package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.consultationpoint.databinding.FragmentDoctorSignupBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoctorSignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class DoctorSignupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDoctorSignupBinding
    private var userModel = UserModel()
    private val viewModel by viewModels<LoginRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener {
            startActivity(
                Intent(activity, LoginActivity::class.java).putExtra(
                    "fromRegister",
                    true
                )
            )
        }

        binding.etFirstName.addTextChangedListener { fName ->

//            enableButton()
        }
        binding.etLastName.addTextChangedListener { lName ->
            userModel.last_name = lName?.trim().toString()
//            enableButton()
        }
        binding.etEmail.addTextChangedListener { email ->
            userModel.email = email?.trim().toString()
//            enableButton()
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(pass: Editable?) {
                val password = pass?.trim().toString()
                if (password.length > 7) {
                    userModel.password = password
//                    enableButton()
                } else {
                    userModel.password = ""
                    binding.etPassword.error = "Please Enter minimum 8 characters"
                    binding.etPassword.requestFocus()
//                    enableButton()
                }
            }
        })

        binding.cbOther.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.etOtherDegree.show()
            else
                binding.etOtherDegree.hide()
        }

        binding.etPhnNo.addTextChangedListener { phnNo ->
            userModel.mobile = phnNo?.trim().toString()
//            enableButton()
        }

        binding.btnSignUp.setOnClickListener {
            checkDetails()
//            activity?.let { it1 -> Utils.showProgressDialog(it1, "We are creating your account") }
        }
    }

    private fun checkDetails() {
        userModel.first_name = binding.etFirstName.text.trim().toString()
        userModel.last_name = binding.etLastName.text.trim().toString()
        userModel.email = binding.etEmail.text.trim().toString()
        userModel.password = binding.etPassword.text.trim().toString()
        userModel.mobile = binding.etPhnNo.text.trim().toString()
//
//        if (cbMBBS.isChecked) {
//            userModel.degrees.add("MBBS")
//        }
//        if (cbBAMS.isChecked) {
//            userModel.degrees.add("BAMS")
//        }
//        if (cbPGDCC.isChecked) {
//            userModel.degrees.add("PGDCC")
//        }
//        if (cbMD.isChecked) {
//            userModel.degrees.add("MD")
//        }
//        if (cbBHMS.isChecked) {
//            userModel.degrees.add("BHMS")
//        }
//        if (cbDM.isChecked) {
//            userModel.degrees.add("DM")
//        }
//        if (cbOther.isChecked) {
//            val otherDegree = binding.etOtherDegree.text.trim().toString()
//            if (otherDegree.isNotEmpty())
//                userModel.degrees.add(otherDegree)
//        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoctorSignupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorSignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}