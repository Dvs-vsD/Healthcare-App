package com.app.consultationpoint.patient.userProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: UserRepository): ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel) {
        repository.updateProfile(model, adrModel)
    }

    fun getProfileUptStatus(): LiveData<String> {
        return repository.getProfileUptStatus()
    }

    fun uploadProfile(path: Uri): StorageReference {
        return repository.uploadProfile(path)
    }
}