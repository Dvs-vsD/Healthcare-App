package com.app.consultationpoint.patient.appointment.bookAppointment

import androidx.lifecycle.LiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import io.realm.Realm
import javax.inject.Inject

class BookAptRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    fun bookAppointment(model: AppointmentModel) {
        firebaseSource.bookAppointment(model)
    }

    fun getDoctorDetails(doctorId: Long): UserModel {
        return firebaseSource.getDoctorDetails(doctorId)
    }

    suspend fun createChatRoom(room: RoomModel, userId: Long, doctorId: Long) {
        firebaseSource.createChatRoom(room, userId, doctorId)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }
}