package com.app.consultationpoint.patient.appointment.myAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAptViewModel @Inject constructor(private val repository: MyAptRepository) : ViewModel() {

    fun fetchAptFromRealm() {
        viewModelScope.launch {
            repository.fetchAptFromRealm()
        }
    }

    fun fetchAptFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAptFromFirebase()
        }
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

    fun fYourAptWithSpecificUser(spUserId: Long) {
        repository.fYourAptWithSpecificUser(spUserId)
    }
}