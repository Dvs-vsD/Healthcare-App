package com.app.consultationpoint.patient.userProfile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityUserProfileBinding
import com.app.consultationpoint.general.LoginActivity
import com.app.consultationpoint.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val viewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inThis()
    }

    private fun inThis() {

        binding.ivBack.setOnClickListener { onBackPressed() }

        getProfileDetails()

        binding.btnEditProfile.setOnClickListener {
            val uptProfileFrag = UpdateProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.clProfile, uptProfileFrag).addToBackStack("Fragment").commit()
        }

        binding.btnLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val bSCount = supportFragmentManager.backStackEntryCount
            if (bSCount == 0) {
                Timber.d("Back Stack Change Listener executed")
                getProfileDetails()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProfileDetails() {
        if (Utils.getUserProfile() != "") {
//            binding.ivProfile.setImageBitmap(Utils.getImageBitMap(this, Utils.getUserProfile()))
        }
        binding.tvUserName.text = Utils.getFirstName() + " " + Utils.getLastName()
        binding.tvUserEmail.text = Utils.getUserEmail()
        val adr = Utils.getUserAdr()
        if (adr != "") {
            binding.tvAddress.setTypeface(null, Typeface.NORMAL)
            binding.tvAddress.text = adr
        }
        val phnNo = Utils.getUserPhnNo()
        if (phnNo != "") {
            binding.tvPhnNo.setTypeface(null, Typeface.NORMAL)
            binding.tvPhnNo.text = phnNo
        }

    }

//    override fun onResume() {
//        super.onResume()
//        Timber.d("resumed")
//        getProfileDetails()
//    }
}