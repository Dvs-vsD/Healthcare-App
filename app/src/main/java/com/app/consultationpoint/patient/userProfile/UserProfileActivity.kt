package com.app.consultationpoint.patient.userProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityUserProfileBinding
import com.app.consultationpoint.general.LoginActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.show
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.Util
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inThis()
    }

    private fun inThis() {

        binding.ivBack.setOnClickListener { onBackPressed() }

        if(Utils.getUserType() == 0)
            binding.professionalDetailsGroup.hide()
        else
            binding.professionalDetailsGroup.show()

        getProfileDetails()

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getProfileDetails()
                Snackbar.make(binding.clProfile, "Profile updated successfully!!!", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, UptPntProfileActivity::class.java)
            startForResult.launch(intent)
        }

        binding.btnLogOut.setOnClickListener {
            Utils.logoutConformationDialog(this)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProfileDetails() {
        val profile = Utils.getUserProfile()
        if (profile != "") {
            binding.ivProfile.loadImageFromCloud(profile)
        }
        val userName = Utils.getUserName()
        val firstName = Utils.getFirstName()
        val lastName = Utils.getLastName()
        binding.tvUserName.text = if (userName != "") {
            userName
        } else {
            "$firstName $lastName"
        }

        binding.tvFirstName.text = firstName
        binding.tvLastName.text = lastName
        binding.tvUserEmail.text = Utils.getUserEmail()
        binding.tvGender.text = when (Utils.getUserGender()) {
            0 -> getString(R.string.rb_male)
            1 -> getString(R.string.rb_female)
            2 -> getString(R.string.rb_other)
            else -> getString(R.string.place_holder)
        }

        val phnNo = Utils.getUserPhnNo()
        if (phnNo != "") {
            binding.tvPhnNo.text = phnNo
        }

        val dob = Utils.getDOB()
        if (dob != "") {
            binding.tvDob.text = dob
        }

        val specialistId = Utils.getSpecialistID()
        if (specialistId != 0) {
            val name = viewModel.getSpecializationName(specialistId)
            if (name != "")
                binding.tvSpecialization.text = name
        }

        val experience = Utils.getExperienceYears()
        if (experience != "")
            binding.tvExperience.text = experience

        val about = Utils.getAboutInfo()
        if (about != "")
            binding.tvAboutInfo.text = about

        val adr = Utils.getUserAdr()
        if (adr != "") {
            binding.tvAddress.text = adr
        }

        val city = Utils.getCity()
        if (city != "")
            binding.tvCity.text = city

        val state = Utils.getState()
        if (state != "")
            binding.tvState.text = state

        val country = Utils.getCountry()
        if (country != "")
            binding.tvCountry.text = country

        val pinCode = Utils.getPinCode()
        if (pinCode != 0)
            binding.tvPinCode.text = pinCode.toString()
    }
}