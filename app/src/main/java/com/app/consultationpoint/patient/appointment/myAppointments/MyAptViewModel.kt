package com.app.consultationpoint.patient.appointment.myAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.patient.doctor.model.DoctorModel

class MyAptViewModel(private val repository: MyAptRepository) : ViewModel() {

    fun fetchAptFromRealm() {
        repository.fetchAptFromRealm()
    }

    fun fetchAptFromFirebase() {
        repository.fetchAptFromFirebase()
    }

    fun getMonthlyAptList(): LiveData<ArrayList<MonthlyAppointments>> {
        return repository.getMonthlyAptList()
    }

    fun getDoctorDetails(doc_id: Long): UserModel {
        return repository.getDoctorDetails(doc_id)
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }
}