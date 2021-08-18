package com.app.consultationpoint.patient.appointment.allAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import java.security.cert.LDAPCertStoreParameters

class AllAptViewModel(private val repository: AllAptRepository) : ViewModel() {

    fun fetchAllEventList() {
        repository.fetchAllEventList()
    }

    fun getALlEventList(): LiveData<ArrayList<AppointmentModel>> {
        return repository.getAllEventList()
    }

    fun getAptForThisDay(date: String) {
        repository.getAptForThisDay(date)
    }

    fun getOneDayApt(): LiveData<ArrayList<AppointmentModel>> {
        return repository.getOneDayAPt()
    }

    fun getAptDoctorList(): LiveData<ArrayList<UserModel>> {
        return repository.getAptDoctorList()
    }

}