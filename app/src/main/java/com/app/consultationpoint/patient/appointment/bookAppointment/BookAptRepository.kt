package com.app.consultationpoint.patient.appointment.bookAppointment

import androidx.lifecycle.LiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import io.realm.Realm

class BookAptRepository(private val firebaseSource: FirebaseSource) {

//    private var mRealm: Realm = Realm.getDefaultInstance()

    fun bookAppointment(model: AppointmentModel) {
        firebaseSource.bookAppointment(model)
    }

    fun getDoctorDetails(doctorId: Long): UserModel {
//        val mRealm: Realm = Realm.getDefaultInstance()
//        val model = UserModel()
//        val result = mRealm.where(UserModel::class.java).equalTo("id", doctorId).findFirst()
//        model.first_name = result?.first_name ?: ""
//        model.last_name = result?.last_name ?: ""
//        model.specialization = result?.specialization ?: ""
//        model.city = result?.city ?: ""
//        model.profile = result?.profile ?: ""
//
//        mRealm.close()

        return firebaseSource.getDoctorDetails(doctorId)
    }

    fun createChatRoom(room: RoomModel, userId: Long, doctorId: Long) {
        firebaseSource.createChatRoom(room, userId, doctorId)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }
}