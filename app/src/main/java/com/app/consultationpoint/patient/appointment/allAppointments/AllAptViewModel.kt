package com.app.consultationpoint.patient.appointment.allAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import kotlinx.coroutines.launch

class AllAptViewModel(private val repository: AllAptRepository) : ViewModel() {

    fun fetchAllEventList() {
        viewModelScope.launch {
            repository.fetchAllEventList()
        }
    }

    fun getALlEventList(): LiveData<ArrayList<AppointmentModel>> {
        return repository.getAllEventList()
    }

    fun getAptForThisDay(date: String) {
        viewModelScope.launch {
            repository.getAptForThisDay(date)
        }
    }

    fun getOneDayApt(): LiveData<ArrayList<AppointmentModel>> {
        return repository.getOneDayAPt()
    }

    fun getAptDoctorList(): LiveData<ArrayList<UserModel>> {
        return repository.getAptDoctorList()
    }

}