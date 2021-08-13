package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityRegisterBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModel<LoginRegisterViewModel>()
    private var userModel = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()

            if (it == "success") {
                if (userModel.user_type_id == 0) {
                    val intent = Intent(this, BottomNavigationActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    intent.putExtra("newUser", true)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Doctor Signed Up successfully", Toast.LENGTH_SHORT).show()
                }
            } else if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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

        binding.btnSignUp.isEnabled = false

        binding.tvLogin.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java).putExtra(
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
            Utils.showProgressDialog(this, "We are creating your account")
            viewModel.signUp(userModel)
        }
    }

    private fun enableButton() {
        binding.btnSignUp.isEnabled = userModel.first_name.isNotEmpty() == true
                && userModel.last_name.isNotEmpty() == true
                && userModel.email.isNotEmpty() == true
                && userModel.password.isNotEmpty() == true
    }
}