package com.app.consultationpoint.patient.appointment.bookAppointment

import androidx.lifecycle.ViewModel
import com.app.consultationpoint.patient.appointment.bookAppointment.BookAptRepository
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel

class BookAptViewModel(private val repository: BookAptRepository) : ViewModel() {

    fun bookAppointment(model: AppointmentModel) {
        repository.bookAppointment(model)
    }

    fun getDoctorDetails(doc_id: String): DoctorModel {
        return repository.getDoctorDetails(doc_id)
    }
}