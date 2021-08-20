package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.app.consultationpoint.databinding.ActivityLoginBinding
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var userEmail: String? = ""
    private var userPassword: String? = ""
    private val viewModel by viewModel<LoginRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it == "success") {
                startActivity()
            } else if (it.startsWith("error")) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        inThat()
    }

    private fun inThat() {

        binding.btnLogin.isEnabled = false

        val fromRegister = intent.getBooleanExtra("fromRegister", false)
        binding.tvSignUp.setOnClickListener {
            if (fromRegister)
                onBackPressed()
            else
                startActivity(Intent(this, RegisterActivity::class.java))
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

        binding.btnLogin.setOnClickListener {
            Utils.showProgressDialog(this)
            viewModel.login(userEmail ?: "", userPassword ?: "")
        }
    }

    private fun enableButton() {
        binding.btnLogin.isEnabled =
            userEmail?.isNotEmpty() == true && userPassword?.isNotEmpty() == true
    }

    private fun startActivity() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        intent.putExtra("newUser", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}