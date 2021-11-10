package com.app.consultationpoint.patient.userProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityUpdateProfileBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.loadImageFromCloud
import com.app.consultationpoint.utils.Utils.loadImageFromCloudWithProgress
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.showToast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class UptPntProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel by viewModels<UserViewModel>()
    private var profileURL: String = ""
    private var profileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getProfileUptStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it.startsWith("url")) {
                profileURL = it.substring(3, it.length)
                binding.ivProfile.loadImageFromCloudWithProgress(profileURL, binding.progressBar)
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

        profileURL = Utils.getUserProfile()
        if (profileURL.isNotEmpty())
            binding.ivProfile.loadImageFromCloud(profileURL)

        if(Utils.getUserType() == 0)
            binding.professionalDetailsGroup.hide()
        else
            binding.professionalDetailsGroup.show()

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

        val specialistArray = viewModel.getSpecialistArray()
        val spNameList: ArrayList<String> = ArrayList()

        spNameList.add(0,getString(R.string.select))

        for ((index,item) in specialistArray.withIndex()) {
            spNameList.add(index + 1 ,item.name)
        }

        binding.spSpecialist.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spNameList)

        val spId = Utils.getSpecialistID()
        if (spId != 0) {
            binding.spSpecialist.setSelection(spId)
        }

        binding.etExperience.setText(Utils.getExperienceYears())
        binding.etAbout.setText(Utils.getAboutInfo())

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
            //Professional detail
            var specialist_id = 0
            if (binding.spSpecialist.selectedItemPosition != 0) {
                val spPosition = binding.spSpecialist.selectedItemPosition - 1
                specialist_id = specialistArray[spPosition].id
            }
            val experience = binding.etExperience.text?.trim().toString()
            val about = binding.etAbout.text?.trim().toString()

            // Address
            val address = binding.etAddress.text?.trim().toString()
            val city = binding.etCity.text?.trim().toString()
            val state = binding.etState.text?.trim().toString()
            val country = binding.etCountry.text?.trim().toString()
            var pincode = 0
            if (binding.etPinCode.text?.trim().toString().isNotEmpty()) {
                pincode = binding.etPinCode.text?.trim().toString().toInt()
            }

            if (userName.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                Utils.setErrorFreeEdt(this, binding.tilUserName)
                Utils.setErrorFreeEdt(this, binding.tilFirstName)
                Utils.setErrorFreeEdt(this, binding.tilLastName)

                val userId = Utils.getUserId().toLong()
                val model = UserModel()
                model.id = userId
                model.doc_id = userId
                model.email = Utils.getUserEmail()
                model.username = userName
                model.first_name = firstName
                model.last_name = lastName
                model.mobile = phnNo
                model.profile = profileURL ?: ""
                model.gender = gender
                model.dob = dob

                model.specialist_id = specialist_id
                model.experience_yr = experience
                model.about_info = about

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

                if (profileUri != null)
                    viewModel.updateProfile(model, adrModel, profileUri!!)
                else
                    viewModel.updateProfile(model, adrModel, null)

                Utils.showProgressDialog(this)
            } else {
                binding.scrollView.smoothScrollTo(0, 0)
                if (userName.isEmpty()) {
                    Utils.setErrorEdt(this, binding.tilUserName)
                    binding.etUserName.requestFocus()
                } else {
                    Utils.setErrorFreeEdt(this, binding.tilUserName)
                }

                if (firstName.isEmpty()) {
                    Utils.setErrorEdt(this, binding.tilFirstName)
                    binding.etFirstName.requestFocus()
                } else {
                    Utils.setErrorFreeEdt(this, binding.tilFirstName)
                }

                if (lastName.isEmpty()) {
                    Utils.setErrorEdt(this, binding.tilLastName)
                    binding.etLastName.requestFocus()
                } else {
                    Utils.setErrorFreeEdt(this, binding.tilLastName)
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
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(40, 40)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                val uri = result.uri
                if (uri != null) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                    profileUri = Utils.getImageUri(this, bitmap) //Compress image
                    binding.ivProfile.setImageURI(profileUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    this.showToast(result.error.toString())
            }
        }
    }
}