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
import com.app.consultationpoint.databinding.FragmentPatientSignupBinding
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PatientSignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PatientSignupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPatientSignupBinding
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var userEmail: String? = ""
    private var userPassword: String? = ""
    private val viewModel by viewModel<LoginRegisterViewModel>()

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
        binding = FragmentPatientSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.isEnabled = false

        binding.tvLogin.setOnClickListener {
            startActivity(
                Intent(activity, LoginActivity::class.java).putExtra(
                    "fromRegister",
                    true
                )
            )
        }

        binding.etFirstName.addTextChangedListener { fName ->
            firstName = fName?.trim().toString()
            enableButton()
        }
        binding.etLastName.addTextChangedListener { lName ->
            lastName = lName?.trim().toString()
            enableButton()
        }
        binding.etEmail.addTextChangedListener { email ->
            userEmail = email?.trim().toString()
            enableButton()
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(pass: Editable?) {
                val password = pass?.trim().toString()
                if (password.length > 7) {
                    userPassword = password
                    enableButton()
                } else {
                    userPassword = ""
                    binding.etPassword.error = "Please Enter minimum 8 characters"
                    binding.etPassword.requestFocus()
                    enableButton()
                }
            }
        })

        binding.btnSignUp.setOnClickListener {
            activity?.let { it1 -> Utils.showProgressDialog(it1, "We are creating your account") }
            viewModel.patientSignUp(firstName ?: "", lastName ?: "", userEmail ?: "", userPassword ?: "")
        }
    }

    private fun enableButton() {
        binding.btnSignUp.isEnabled = firstName?.isNotEmpty() == true
                && lastName?.isNotEmpty() == true
                && userEmail?.isNotEmpty() == true
                && userPassword?.isNotEmpty() == true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PatientSignupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PatientSignupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}