package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.app.consultationpoint.databinding.FragmentPatientSignupBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PatientSignupFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPatientSignupBinding
    private val viewModel by viewModel<LoginRegisterViewModel>()
    private var userModel = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()

            if (it == "success") {
                val intent = Intent(activity, BottomNavigationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else if (it.isNotEmpty()) {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        })
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
            userModel.first_name = fName?.trim().toString()
            enableButton()
        }
        binding.etLastName.addTextChangedListener { lName ->
            userModel.last_name = lName?.trim().toString()
            enableButton()
        }
        binding.etEmail.addTextChangedListener { email ->
            userModel.email = email?.trim().toString()
            enableButton()
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(pass: Editable?) {
                val password = pass?.trim().toString()
                if (password.length > 7) {
                    userModel.password = password
                    enableButton()
                } else {
                    userModel.password = ""
                    binding.etPassword.error = "Please Enter minimum 8 characters"
                    binding.etPassword.requestFocus()
                    enableButton()
                }
            }
        })

        binding.btnSignUp.setOnClickListener {
            activity?.let { it1 -> Utils.showProgressDialog(it1, "We are creating your account") }
            viewModel.patientSignUp(userModel)
        }
    }

    private fun enableButton() {
        binding.btnSignUp.isEnabled = userModel.first_name?.isNotEmpty() == true
                && userModel.last_name?.isNotEmpty() == true
                && userModel.email?.isNotEmpty() == true
                && userModel.password?.isNotEmpty() == true
    }

    companion object {

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