package com.app.consultationpoint.patient.userProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel

class UserViewModel(private val repository: UserRepository): ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel) {
        repository.updateProfile(model, adrModel)
    }

    fun getProfileUptStatus(): LiveData<String> {
        return repository.getProfileUptStatus()
    }
}