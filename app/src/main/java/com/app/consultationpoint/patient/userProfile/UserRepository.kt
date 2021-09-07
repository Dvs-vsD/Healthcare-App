package com.app.consultationpoint.patient.userProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class UserRepository @Inject constructor(private var firebaseSource: FirebaseSource) {
    fun logout() {
        firebaseSource.logOut()
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel) {
        firebaseSource.updateProfile(model, adrModel)
    }

    fun getProfileUptStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun uploadProfile(path: Uri): StorageReference {
        return firebaseSource.uploadProfile(path)
    }
}