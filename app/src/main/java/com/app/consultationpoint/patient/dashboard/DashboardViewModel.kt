package com.app.consultationpoint.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository: DashboardRepository) : ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun fetchAllMyBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchAllMyBookings()
        }
    }

    fun fetchTodayApt(today: String) {
//        viewModelScope.launch {
            repository.fetchTodayApt(today)
//        }
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

    fun fetchDocFromFB(type_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchDoctorsFromFB(type_id)
        }
    }
}