    package com.app.consultationpoint.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import java.security.cert.LDAPCertStoreParameters
import java.util.*
import kotlin.collections.ArrayList

class DashboardViewModel(private val repository: DashboardRepository): ViewModel() {
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
        repository.fetchSpItemsFromFB()
    }

    fun fetchSpItemsFromRDB() {
        repository.fetchSpItemsFromRDB()
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