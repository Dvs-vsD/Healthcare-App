package com.app.consultationpoint.general

import androidx.lifecycle.LiveData
import com.app.consultationpoint.firebase.FirebaseSource

class LoginRegisterRepository(private val firebaseSource: FirebaseSource) {
    fun patientSignUp(fName: String, lName: String, email: String, pass: String) {
        firebaseSource.signUp(fName, lName, email, pass)
    }

    fun getRegistrationStatus(): LiveData<String> {
        return firebaseSource.getRegistrationStatus()
    }

    fun login(email: String, password: String) {
        firebaseSource.login(email, password)
    }

    fun isLogin(): Boolean {
        return firebaseSource.isLogin()
    }

}