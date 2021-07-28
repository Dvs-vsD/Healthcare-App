package com.app.consultationpoint.patient.userProfile

import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel

class UserViewModel(private val repository: UserRepository): ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun updateProfile(model: UserModel): String {
        return repository.updateProfile(model)
    }
}