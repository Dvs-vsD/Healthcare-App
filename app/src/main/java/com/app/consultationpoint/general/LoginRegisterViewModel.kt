package com.app.consultationpoint.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginRegisterViewModel(private val repository: LoginRegisterRepository) : ViewModel() {

    fun signUp(model: UserModel) {
        viewModelScope.launch {
            repository.signUp(model)
        }
    }

    fun getRegistrationStatus(): LiveData<String> {
        return repository.getRegistrationStatus()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
        }
    }
}