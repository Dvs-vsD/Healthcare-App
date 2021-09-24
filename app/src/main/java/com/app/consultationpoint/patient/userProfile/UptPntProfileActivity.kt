package com.app.consultationpoint.patient.userProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.app.consultationpoint.GlideApp
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityUpdateProfileBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.utils.Const.REQUEST_CODE
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.loadImage
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class UptPntProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel by viewModels<UserViewModel>()
    private var profileURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getProfileUptStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it.startsWith("url")) {
                profileURL = it.substring(3, it.length)
                binding.ivProfile.loadImageFromCloud(profileURL?:"")
            } else if (it == "profile upload failed") {
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

        viewModel.getPostalData().observe(this, {
            if (it != null) {
                binding.etCity.setText(it.District)
                binding.etState.setText(it.State)
                binding.etCountry.setText(it.Country)
            }
        })

        inThis()
    }

    @SuppressLint("TimberArgCount")
    private fun inThis() {
        val profile = Utils.getUserProfile()
        if (profile.isNotEmpty())
            binding.ivProfile.loadImageFromCloud(profile)

        binding.etUserName.setText(Utils.getUserName())
        binding.etFirstName.setText(Utils.getFirstName())
        binding.etLastName.setText(Utils.getLastName())
        binding.etPhnNo.setText(Utils.getUserPhnNo())
        binding.etEmail.setText(Utils.getUserEmail())

        when (Utils.getUserGender()) {
            0 -> binding.rbMale.isChecked = true
            1 -> binding.rbFemale.isChecked = true
            2 -> binding.rbOther.isChecked = true
        }

        binding.etDob.setText(Utils.getDOB())
        binding.etAddress.setText(Utils.getUserAdr())
        binding.etCity.setText(Utils.getCity())
        binding.etState.setText(Utils.getState())
        binding.etCountry.setText(Utils.getCountry())

        if (Utils.getPinCode() != 0)
            binding.etPinCode.setText(Utils.getPinCode().toString())

        binding.etPinCode.addTextChangedListener {
            if (it?.length == 6) {
//                binding.tilPinCode.error = ""
                viewModel.fetchPostalData(it.toString())
            }/* else if (it?.isNotEmpty() == true) {
                binding.tilPinCode.error = "Pin code must be 6 digit long!!!"
            } else if (it?.isEmpty() == true) {
                binding.tilPinCode.error = ""
            }*/
        }

        binding.ivChoosePic.setOnClickListener {
            checkPermissions()
        }

        binding.ivProfile.setOnClickListener {
            checkPermissions()
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

            var gender: Int = -1
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
            var pincode = 0
            if (binding.etPinCode.text?.trim().toString().isNotEmpty()) {
                pincode = binding.etPinCode.text?.trim().toString().toInt()
            }

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
                model.profile = profileURL?:""
                model.gender = gender
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

    private fun checkPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        val picImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        picImage.type = "image/*"
//        picImage.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(picImage, "Select Profile Pic..."),
            REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data

                if (uri != null) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                    val profilePic = Utils.getImageUri(this, bitmap)
                    Utils.showProgressDialog(this)
                    if (profilePic != null) {
                        viewModel.uploadProfile(profilePic)
                    }
                } else
                    Toast.makeText(this, "Image Not selected: Please Reselect", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
}