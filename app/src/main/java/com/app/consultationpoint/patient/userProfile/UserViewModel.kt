package com.app.consultationpoint.patient.userProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.patient.userProfile.model.PostalDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel, path: Uri?) {
        repository.updateProfile(model, adrModel, path)
    }

    fun getProfileUptStatus(): LiveData<String> {
        return repository.getProfileUptStatus()
    }

    /*fun uploadProfile(path: Uri) {
        repository.uploadProfile(path)
    }*/

    fun fetchPostalData(it: String) {
        return repository.fetchPostalData(it)
    }

    fun getPostalData(): LiveData<PostalDataModel> {
        return repository.getPostalData()
    }

    fun getSpecialistArray(): ArrayList<SpecialistModel> {
        return repository.getSpecialistArray()
    }

    fun getSpecializationName(id: Int): String {
        return repository.getSpecializationName(id)
    }
}