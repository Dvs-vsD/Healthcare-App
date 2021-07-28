package com.app.consultationpoint.patient.userProfile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.consultationpoint.databinding.FragmentUpdateProfileBinding
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.utils.Utils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_update_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        binding.etFirstName.setText(Utils.getFirstName())
        binding.etLastName.setText(Utils.getLastName())

        binding.tvChangeProfile.setOnClickListener {
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

        binding.ivCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnUpdate.setOnClickListener {
            val firstName = etFirstName.text.trim().toString()
            val lastName = etLastName.text.trim().toString()
            val address = etAddress.text.trim().toString()
            val phnNo = etPhnNo.text.trim().toString()
            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                val model = UserModel()
                model.user_id = Utils.getUserId()
                model.email = Utils.getUserEmail()
                model.first_name = firstName
                model.last_name = lastName
                model.address = address
                model.phone_no = phnNo
                model.profile = profile

                val status = viewModel.updateProfile(model)

                if (status != "") {
                    Toast.makeText(activity, status, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    parentFragmentManager.popBackStack()
                }
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

    private fun capturePhoto() {
        val picImage = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
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