package com.app.consultationpoint.patient.userProfile

import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel

class UserRepository(private var firebaseSource: FirebaseSource) {
    fun logout() {
        firebaseSource.logOut()
    }

    fun updateProfile(model: UserModel): String {
        return firebaseSource.updateProfile(model)
    }
}