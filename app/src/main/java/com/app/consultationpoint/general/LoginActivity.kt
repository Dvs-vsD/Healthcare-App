package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.databinding.ActivityLoginBinding
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.BiometricUtils
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), BiometricUtils.BiometricAuthListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginRegisterViewModel>()
    private var fromOnBoarding: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it == "login success") {

                ConsultationApp.createRealmDB()

                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.putExtra("newUser", true)
                startActivity(intent)
                finish()
            } else if (it.startsWith("Error")) {
                this.showToast("Error: $it")
            }
        })

        inThat()
    }

    private fun inThat() {

        fromOnBoarding = intent.getBooleanExtra("fromOnBoarding", false)

        checkBiometricAvailability()

        binding.tvEnrollBiometric.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_BIOMETRIC_ENROLL))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            checkValidation()
        }

        binding.btnLoginWithBiometric.setOnClickListener {
            if (ConsultationApp.shPrefGlobal.getBoolean(Const.IS_BIOMETRIC_ADDED, false))
                showBiometricPromptDialog()
            else {
                startActivity(Intent(this, AddBiometricActivity::class.java))
            }
        }
    }

    private fun showBiometricPromptDialog() {
        BiometricUtils.showBiometricPrompt(
            activity = this,
            listener = this,
            cryptoObject = null,
            allowDeviceCredential = true
        )
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

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        this.showToast(errString.toString())
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        Utils.showProgressDialog(this)

        val email = ConsultationApp.shPrefGlobal.getString(
            Const.BIOMETRIC_EMAIL, ""
        )
        val password = ConsultationApp.shPrefGlobal.getString(
            Const.BIOMETRIC_PASSWORD, ""
        )
        viewModel.login(email ?: "", password ?: "")
    }

    override fun onResume() {
        checkBiometricAvailability()
        super.onResume()
    }

    private fun checkBiometricAvailability() {
        when {
            BiometricUtils.isBiometricReady(this) -> {
                if (fromOnBoarding && ConsultationApp.shPrefGlobal.getBoolean(
                        Const.IS_BIOMETRIC_ADDED,
                        false
                    )
                ) {
                    showBiometricPromptDialog()
                }
                binding.tvOr.show()
                binding.btnLoginWithBiometric.show()
                binding.tvEnrollBiometric.hide()
            }
            BiometricUtils.isBiometricNotEnrolled(this) -> {
                binding.tvOr.show()
                binding.btnLoginWithBiometric.hide()
                binding.tvEnrollBiometric.show()
            }
            else -> {
                binding.tvOr.hide()
                binding.btnLoginWithBiometric.hide()
                binding.tvEnrollBiometric.hide()
            }
        }
    }
}