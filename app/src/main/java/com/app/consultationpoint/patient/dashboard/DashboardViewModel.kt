package com.app.consultationpoint.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import java.util.*
import kotlin.collections.ArrayList

class DashboardViewModel(private val repository: DashboardRepository): ViewModel() {
    fun logout() {
        repository.logout()
    }

    fun addDoctorList(list: ArrayList<DoctorModel>) {
        repository.addDoctorList(list)
    }

    fun init() {
        repository.init()
    }

    fun getTodayApt(today: String) {
        repository.fetchTodayApt(today)
    }

    fun getTodayAptList(): LiveData<ArrayList<AppointmentModel>> {
        return repository.getTodayAptList()
    }

    fun getDoctorDetails(docId: Long): UserModel {
        return repository.getDoctorDetails(docId)
    }
}