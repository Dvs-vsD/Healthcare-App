package com.app.consultationpoint.patient.appointment.bookAppointment

import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import io.realm.Realm

class BookAptRepository(private val firebaseSource: FirebaseSource) {

    private var mRealm: Realm = Realm.getDefaultInstance()

    fun bookAppointment(model: AppointmentModel) {
        firebaseSource.bookAppointment(model)
    }

    fun getDoctorDetails(doc_id: String): DoctorModel {
        val model = DoctorModel()
        val result = mRealm.where(DoctorModel::class.java).equalTo("doc_id", doc_id).findFirst()
        model.first_name = result?.first_name ?: ""
        model.last_name = result?.last_name ?: ""
        model.specialization = result?.specialization ?: ""
        model.city = result?.city ?: ""
        model.profile = result?.profile?:""

        return model
    }
}