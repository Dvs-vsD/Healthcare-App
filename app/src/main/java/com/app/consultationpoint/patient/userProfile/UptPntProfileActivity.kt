package com.app.consultationpoint.patient.userProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityUpdateProfileBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.utils.Const.REQUEST_CODE
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.loadImage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class UptPntProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private var profile: String? = ""
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getProfileUptStatus().observe(this, {
            Utils.dismissProgressDialog()
            /*if (it.startsWith("url")) {
                profile = it.substring(3, it.length)
                Timber.d("Profile Url: %s",profile)
                binding.ivProfile.loadImage(profile?:"")
            } else */if (it == "profile upload failed") {
                Toast.makeText(this, "Failed: Please reselect image", Toast.LENGTH_LONG).show()
            }
            if (it == "Profile Updated") {
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else if (it.startsWith("error")) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        })

        inThis()
    }

    @SuppressLint("TimberArgCount")
    private fun inThis() {
//
//        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/alpha-healthcare.appspot.com/o/images%2F1628673623956.jpg?alt=media&token=c53638ba-ae3b-4446-adff-86fd5de95af9")
//            .listener(object : RequestListener<Drawable?> {
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable?>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable?>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//            }).centerCrop().into(binding.ivProfile)
        //binding.ivProfile.loadImage("https://firebasestorage.googleapis.com/v0/b/alpha-healthcare.appspot.com/o/images%2F1628673623956.jpg?alt=media&token=c53638ba-ae3b-4446-adff-86fd5de95af9")

        binding.etUserName.setText(Utils.getUserName())
        binding.etFirstName.setText(Utils.getFirstName())
        binding.etLastName.setText(Utils.getLastName())
        binding.etPhnNo.setText(Utils.getUserPhnNo())

        when (Utils.getUserGender()) {
            "0" -> binding.rbMale.isChecked = true
            "1" -> binding.rbFemale.isChecked = true
            "2" -> binding.rbOther.isChecked = true
        }

        binding.etDob.setText(Utils.getDOB())
        binding.etAddress.setText(Utils.getUserAdr())
        binding.etCity.setText(Utils.getCity())
        binding.etState.setText(Utils.getState())
        binding.etCountry.setText(Utils.getCountry())
        binding.etPinCode.setText(Utils.getPinCode().toString())

        binding.ivChoosePic.setOnClickListener {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {
                            chooseImage()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }

        binding.etDob.setOnClickListener {
            datePickerDialog()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val userName = binding.etUserName.text?.trim().toString()
            val firstName = binding.etFirstName.text?.trim().toString()
            val lastName = binding.etLastName.text?.trim().toString()
            val phnNo = binding.etPhnNo.text?.trim().toString()

            var gender: Int? = null
            when (binding.rgGender.checkedRadioButtonId) {
                R.id.rbMale -> gender = 0
                R.id.rbFemale -> gender = 1
                R.id.rbOther -> gender = 2
            }
            val dob = binding.etDob.text?.trim().toString()

            // Address
            val address = binding.etAddress.text?.trim().toString()
            val city = binding.etCity.text?.trim().toString()
            val state = binding.etState.text?.trim().toString()
            val country = binding.etCountry.text?.trim().toString()
            val pincode = binding.etPinCode.text?.trim().toString().toInt()

            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                val userId = Utils.getUserId().toLong()
                val model = UserModel()
                model.id = userId
                model.doc_id = userId
                model.email = Utils.getUserEmail()
                model.username = userName
                model.first_name = firstName
                model.last_name = lastName
                model.mobile = phnNo
                model.profile = profile
                if (gender != null) {
                    model.gender = gender
                }
                model.dob = dob
                model.user_type_id = Utils.getUserType()
                model.created_at = userId
                model.updated_at = System.currentTimeMillis()

                val adrModel = AddressModel()
                adrModel.user_id = Utils.getUserId().toLong()
                adrModel.address = address
                adrModel.city = city
                adrModel.state = state
                adrModel.country = country
                adrModel.pincode = pincode
                adrModel.created_at = userId
                adrModel.updated_at = System.currentTimeMillis()

                viewModel.updateProfile(model, adrModel)
                Utils.showProgressDialog(this)
            } else {
                if (firstName.isEmpty()) {
                    binding.etFirstName.error = "Please enter first name!!!"
                    binding.etFirstName.requestFocus()
                } else {
                    binding.etLastName.error = "Please enter end name!!!"
                    binding.etLastName.requestFocus()
                }
            }
        }
    }

    private fun datePickerDialog() {
        val cal = Calendar.getInstance()
        val cYear = cal.get(Calendar.YEAR)
        val cMonth = cal.get(Calendar.MONTH)
        val cDay = cal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                cal[Calendar.YEAR] = year
                cal[Calendar.MONTH] = month
                cal[Calendar.DAY_OF_MONTH] = dayOfMonth
                val dob = cal.time.formatTo("dd-MM-yyyy")
                binding.etDob.setText(dob)
            }, cYear, cMonth, cDay)

        datePickerDialog.show()
    }

    private fun chooseImage() {
        val picImage = Intent()
        picImage.type = "image/*"
        picImage.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(picImage, "Select Profile Pic..."),
            REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                val profileUri = data.data
                if (profileUri != null) {
                    val ref = viewModel.uploadProfile(profileUri)
                    Glide.with(this).load(ref).into(binding.ivProfile)
                } else
                    Toast.makeText(this, "Image Not selected: Please Reselect", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
}