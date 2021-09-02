package com.app.consultationpoint.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun fetchTodayApt(today: String) {
        repository.fetchTodayApt(today)
    }

    fun getTodayAptList(): LiveData<ArrayList<AppointmentModel>> {
        return repository.getTodayAptList()
    }

    fun getAPtDoctorList(): LiveData<ArrayList<UserModel>> {
        return repository.getAptDoctorList()
    }

    fun getDoctorDetails(docId: Long): UserModel {
        return repository.getDoctorDetails(docId)
    }

    fun fetchSpFromFB() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchSpItemsFromFB()
        }
    }

    fun fetchSpItemsFromRDB() {
        viewModelScope.launch {
            repository.fetchSpItemsFromRDB()
        }
    }

    fun getSpCategoryList(): LiveData<ArrayList<SpecialistModel>> {
        return repository.getSpCategoryList()
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }

    fun fetchALlAptFromRDB() {
        repository.fetchAllAptFromRDB()
    }

    fun getAptDateList(): LiveData<ArrayList<Calendar>> {
        return repository.getAptDateList()
    }
}