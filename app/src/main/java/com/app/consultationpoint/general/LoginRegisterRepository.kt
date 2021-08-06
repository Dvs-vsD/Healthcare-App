package com.app.consultationpoint.general

import androidx.lifecycle.LiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel

class LoginRegisterRepository(private val firebaseSource: FirebaseSource) {
    fun signUp(model: UserModel) {
        firebaseSource.signUp(model)
    }

    fun getRegistrationStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun login(email: String, password: String) {
        firebaseSource.login(email, password)
    }
}