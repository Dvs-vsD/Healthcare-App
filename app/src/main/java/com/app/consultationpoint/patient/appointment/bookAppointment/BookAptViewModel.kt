package com.app.consultationpoint.patient.appointment.bookAppointment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.bookAppointment.BookAptRepository
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel

class BookAptViewModel(private val repository: BookAptRepository) : ViewModel() {

    fun bookAppointment(model: AppointmentModel) {
        repository.bookAppointment(model)
    }

    fun getDoctorDetails(doctorId: Long): UserModel {
        return repository.getDoctorDetails(doctorId)
    }

    fun createChatRoom(room: RoomModel, userId: Long, doctorId: Long) {
        repository.createChatRoom(room, userId, doctorId)
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }
}