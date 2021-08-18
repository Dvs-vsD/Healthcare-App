package com.app.consultationpoint.patient.userProfile

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.FragmentUpdateProfileBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.google.type.Date
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_update_profile.*
import okhttp3.internal.Util
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UpdateProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUpdateProfileBinding
    private var profile: String? = ""
    private val viewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel.getProfileUptStatus().observe(this, {
            Utils.dismissProgressDialog()
            if (it == "Profile Updated") {
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT)
                    .show()
                parentFragmentManager.popBackStack()
            } else if (it != "") {
                Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("userId %s", Utils.getUserId())

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
            Dexter.withContext(activity)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {
                            capturePhoto()
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

        binding.ivCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val userName = etUserName.text?.trim().toString()
            val firstName = etFirstName.text?.trim().toString()
            val lastName = etLastName.text?.trim().toString()
            val phnNo = etPhnNo.text?.trim().toString()

            var gender: Int? = null
            when (rgGender.checkedRadioButtonId) {
                R.id.rbMale -> gender = 0
                R.id.rbFemale -> gender = 1
                R.id.rbOther -> gender = 2
            }
            val dob = etDob.text?.trim().toString()

            // Address
            val address = etAddress.text?.trim().toString()
            val city = etCity.text?.trim().toString()
            val state = etState.text?.trim().toString()
            val country = etCountry.text?.trim().toString()
            val pincode = etPinCode.text?.trim().toString().toInt()

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
                activity?.let { it1 -> Utils.showProgressDialog(it1,"") }
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
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            activity?.let {
                DatePickerDialog(it, { _, year, month, dayOfMonth ->
                    cal[Calendar.YEAR] = year
                    cal[Calendar.MONTH] = month
                    cal[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val dob = cal.time.formatTo("dd-MM-yyyy")
                    binding.etDob.setText(dob)
                }, year, month, day)
            }

        datePickerDialog?.show()
    }

    private fun capturePhoto() {
        val picImage = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        picImage.type = "image/*"
        startActivityForResult(picImage, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                val profileUri = data?.data
                binding.ivProfile.setImageURI(profileUri)
                profile = profileUri.toString()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}