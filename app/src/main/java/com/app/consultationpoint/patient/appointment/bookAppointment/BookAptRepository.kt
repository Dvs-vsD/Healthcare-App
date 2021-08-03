package com.app.consultationpoint.patient.appointment.bookAppointment

import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import io.realm.Realm

class BookAptRepository(private val firebaseSource: FirebaseSource) {

    private var mRealm: Realm = Realm.getDefaultInstance()

    fun bookAppointment(model: AppointmentModel) {
        firebaseSource.bookAppointment(model)
    }
}