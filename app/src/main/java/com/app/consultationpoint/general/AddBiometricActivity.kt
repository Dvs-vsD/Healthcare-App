package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.databinding.ActivityAddBiometricBinding
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.BiometricUtils
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBiometricActivity : AppCompatActivity(), BiometricUtils.BiometricAuthListener {

    private lateinit var binding: ActivityAddBiometricBinding
    private val viewModel by viewModels<LoginRegisterViewModel>()
    private var userEmail: String = ""
    private var userPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it == "login success") {

                val editor = ConsultationApp.shPrefGlobal.edit()
                editor.putBoolean(Const.IS_BIOMETRIC_ADDED, true)
                editor.putString(Const.BIOMETRIC_EMAIL, userEmail)
                editor.putString(Const.BIOMETRIC_PASSWORD, userPassword)
                editor.apply()

                val intent = Intent(this, BottomNavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
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

        binding.btnLoginWithPassword.setOnClickListener {
            onBackPressed()
        }

        binding.btnLoginWithBiometric.setOnClickListener {
            checkValidation()
        }
    }

    private fun checkValidation() {

        userEmail = binding.etEmail.text?.trim().toString()
        userPassword = binding.etPassword.text?.trim().toString()

        if (userEmail.isNotEmpty() && userPassword.isNotEmpty() && userPassword.length > 7) {
            Utils.setErrorFreeEdt(this, binding.tilEmail)
            Utils.setErrorFreeEdt(this, binding.tilPassword)
            binding.tilPassword.error = ""

            BiometricUtils.showBiometricPrompt(
                activity = this,
                listener = this,
                cryptoObject = null,
                allowDeviceCredential = true
            )
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

        if (Utils.isNetworkAvailable(this)) {
            viewModel.login(userEmail, userPassword)
        } else {
            Utils.dismissProgressDialog()
            Snackbar.make(
                this,
                binding.clLogin,
                "Please check your network connection!!!",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}