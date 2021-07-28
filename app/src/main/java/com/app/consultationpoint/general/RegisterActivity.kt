package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityRegisterBinding
import com.app.consultationpoint.patient.bottomNavigation.BottomNavigationActivity
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModel<LoginRegisterViewModel>()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.isLogin()) {
            startActivity()
        }

        viewModel.getRegistrationStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it == "success") {
                startActivity()
            } else if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        inThat()
    }

    private fun inThat() {

        val fragment = PatientSignupFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment).commit()

        binding.rgPntOrDoc.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rbPatient) {
                val fragPntSignUp = PatientSignupFragment()
                val transactionPnt: FragmentTransaction = supportFragmentManager.beginTransaction()
                transactionPnt.replace(R.id.frameLayout, fragPntSignUp).commit()
            } else {
                val fragDocSignUp = DoctorSignupFragment()
                val transactionDoc: FragmentTransaction = supportFragmentManager.beginTransaction()
                transactionDoc.replace(R.id.frameLayout, fragDocSignUp).commit()
            }
        }
    }


    private fun startActivity() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}