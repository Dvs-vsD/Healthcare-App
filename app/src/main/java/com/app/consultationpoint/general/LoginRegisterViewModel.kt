package com.app.consultationpoint.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel

class LoginRegisterViewModel(private val repository: LoginRegisterRepository) : ViewModel() {

    fun signUp(model: UserModel) {
        repository.signUp(model)
    }

    fun getRegistrationStatus(): LiveData<String> {
        return repository.getRegistrationStatus()
    }

    fun login(email: String, password: String) {
        repository.login(email, password)
    }

    fun isLogin(): Boolean {
        return repository.isLogin()
    }
}