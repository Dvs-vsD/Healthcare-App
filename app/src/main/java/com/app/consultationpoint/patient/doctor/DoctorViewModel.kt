package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel

class DoctorViewModel(private val repository: DoctorRepository): ViewModel() {

    fun getDoctorList() : LiveData<ArrayList<DoctorModel>>{
        return repository.getDoctorList()
    }

    fun searchDoctor(str: String) {
        repository.searchDoctor(str)
    }
}