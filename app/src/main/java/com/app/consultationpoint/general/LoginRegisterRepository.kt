package com.app.consultationpoint.general

import androidx.lifecycle.LiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import javax.inject.Inject

class LoginRegisterRepository @Inject constructor(private val firebaseSource: FirebaseSource) {
    suspend fun signUp(model: UserModel) {
        firebaseSource.signUp(model)
    }

    fun getRegistrationStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    suspend fun login(email: String, password: String) {
        firebaseSource.login(email, password)
    }
}