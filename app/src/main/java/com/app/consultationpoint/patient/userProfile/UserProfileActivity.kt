package com.app.consultationpoint.patient.userProfile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityUserProfileBinding
import com.app.consultationpoint.general.LoginActivity
import com.app.consultationpoint.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import androidx.core.net.toUri as toUri

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
            val bundle = Bundle()
            if (tvAddress.text != getString(R.string.place_holder_address))
                bundle.putString("address", tvAddress.text.toString())
            if (tvPhnNo.text != getString(R.string.place_holder_phone_number))
                bundle.putString("address", tvPhnNo.text.toString())
            uptProfileFrag.arguments = bundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.clProfile, uptProfileFrag).addToBackStack("Fragment").commit()
        }

        binding.btnLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProfileDetails() {
        if (Utils.getUserProfile() != "") {
//            val profileUri = Utils.getUserProfile().toUri()
            val profileUri = Uri.parse(Utils.getUserProfile())
            binding.ivProfile.setImageURI(profileUri)
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