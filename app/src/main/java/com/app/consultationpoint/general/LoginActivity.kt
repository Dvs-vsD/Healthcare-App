package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.databinding.ActivityLoginBinding
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it == "login success") {
                startActivity()
            } else if (it.startsWith("Error")) {
                this.showToast("Error: $it")
            }
        })

        inThat()
    }

    private fun inThat() {

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            checkValidation()
        }
    }

    private fun checkValidation() {

        val userEmail = binding.etEmail.text?.trim().toString()
        val userPassword = binding.etPassword.text?.trim().toString()

        if (userEmail.isNotEmpty() && userPassword.isNotEmpty() && userPassword.length > 7) {
            Utils.setErrorFreeEdt(this, binding.tilEmail)
            Utils.setErrorFreeEdt(this, binding.tilPassword)
            binding.tilPassword.error = ""

            Utils.showProgressDialog(this)

            if (Utils.isNetworkAvailable(this)) {
                viewModel.login(userEmail ?: "", userPassword ?: "")
            } else {
                Utils.dismissProgressDialog()
                Snackbar.make(
                    this,
                    binding.clLogin,
                    "Please check your network connection!!!",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        } else {

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

    private fun startActivity() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        intent.putExtra("newUser", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}