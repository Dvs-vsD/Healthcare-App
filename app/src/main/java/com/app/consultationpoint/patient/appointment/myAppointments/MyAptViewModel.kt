package com.app.consultationpoint.patient.appointment.myAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.patient.doctor.model.DoctorModel

class MyAptViewModel(private val repository: MyAptRepository) : ViewModel() {

    fun initRepo() {
        repository.init()
    }
    fun getMonthlyAptList(): LiveData<ArrayList<MonthlyAppointments>> {
        return repository.getMonthlyAptList()
    }

    fun getDoctorDetails(doc_id: String): DoctorModel {
        return repository.getDoctorDetails(doc_id)
    }
}