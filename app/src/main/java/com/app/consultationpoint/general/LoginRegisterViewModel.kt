package com.app.consultationpoint.general

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LoginRegisterViewModel(private val repository: LoginRegisterRepository) : ViewModel() {

    fun patientSignUp(fName: String, lName: String, email: String, pass: String) {
        repository.patientSignUp(fName, lName, email, pass)
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