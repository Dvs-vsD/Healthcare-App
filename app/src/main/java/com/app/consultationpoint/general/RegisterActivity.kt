package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityRegisterBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<LoginRegisterViewModel>()
    private var userModel = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()

            if (it == "success") {
//                if (userModel.user_type_id == 0) {
                ConsultationApp.shPrefGlobal.edit().clear().apply()

                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("newUser", true)
                intent.putExtra("fromRegister", true)
                startActivity(intent)
//                } else {
                Toast.makeText(this, "Sign Up successful", Toast.LENGTH_SHORT).show()
//                }
            } else if (it.startsWith("Error: ")) {
                this.showToast("Error: $it")
            }
        })

        inThat()
    }

    private fun inThat() {

        binding.rgPntOrDoc.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbPatient)
                userModel.user_type_id = 0
            else
                userModel.user_type_id = 1
        }

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbMale -> userModel.gender = 0
                R.id.rbFemale -> userModel.gender = 1
                R.id.rbOther -> userModel.gender = 2
            }
        }

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            checkValidation()
        }
    }

    private fun checkValidation() {

        val userName = binding.etUserName.text?.trim().toString()
        val firstName = binding.etFirstName.text?.trim().toString()
        val lastName = binding.etLastName.text?.trim().toString()
        val userEmail = binding.etEmail.text?.trim().toString()
        val userPassword = binding.etPassword.text?.trim().toString()

        if (userName.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && userModel.gender != -1 && userEmail.isNotEmpty()
            && userPassword.isNotEmpty() && userPassword.length > 7
        ) {
            Utils.setErrorFreeEdt(this, binding.tilUserName)
            Utils.setErrorFreeEdt(this, binding.tilFirstName)
            Utils.setErrorFreeEdt(this, binding.tilLastName)
            binding.tvErrorTextGender.hide()
            Utils.setErrorFreeEdt(this, binding.tilEmail)
            Utils.setErrorFreeEdt(this, binding.tilPassword)
            binding.tilPassword.error = ""

            Utils.showProgressDialog(this)

            if (Utils.isNetworkAvailable(this)) {
                userModel.username = userName
                userModel.first_name = firstName
                userModel.last_name = lastName
                userModel.email = userEmail
                userModel.password = userPassword
                userModel.gender = userModel.gender
                userModel.device_type = 1

                viewModel.signUp(userModel)
            } else {
                Utils.dismissProgressDialog()
                Snackbar.make(
                    this,
                    binding.svRegister,
                    "Please check your network connection!!!",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        } else {

            if (userName.isEmpty()) {
                Utils.setErrorEdt(this, binding.tilUserName)
            } else {
                Utils.setErrorFreeEdt(this, binding.tilUserName)
            }

            if (firstName.isEmpty()) {
                Utils.setErrorEdt(this, binding.tilFirstName)
            } else {
                Utils.setErrorFreeEdt(this, binding.tilFirstName)
            }

            if (lastName.isEmpty()) {
                Utils.setErrorEdt(this, binding.tilLastName)
            } else {
                Utils.setErrorFreeEdt(this, binding.tilLastName)
            }

            if (userModel.gender == -1) {
                binding.tvErrorTextGender.show()
            } else {
                binding.tvErrorTextGender.hide()
            }

            if (userEmail.isEmpty()) {
                Utils.setErrorEdt(this, binding.tilEmail)
            } else {
                Utils.setErrorFreeEdt(this, binding.tilEmail)
            }

            if (userPassword.isEmpty()) {
                Utils.setErrorEdt(this, binding.tilPassword)
            } else {
                if (userPassword.length < 8) {
                    binding.tilPassword.error =
                        "Password must be more then or equal to 8 characters"
                    Utils.setErrorEdt(this, binding.tilPassword)
                } else {
                    Utils.setErrorFreeEdt(this, binding.tilPassword)
                    binding.tilPassword.error = ""
                }
            }
        }
    }
}