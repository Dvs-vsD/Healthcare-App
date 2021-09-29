package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DoctorViewModel @Inject constructor(private val repository: DoctorRepository) : ViewModel() {

    fun fetchDocFromFB(userType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchDocFromFB(userType)
        }
    }

    fun fetchDocFromRDB() {
        viewModelScope.launch {
            repository.fetchDocFromRDB()
        }
    }

    fun getDoctorList(): MutableLiveData<ArrayList<UserModel>> {
        return repository.getDoctorList()
    }

    fun searchDoctor(str: String) {
        repository.searchDoctor(str)
    }

    fun getDoctorDetails(doc_id: Long): UserModel {
        return repository.getDoctorDetails(doc_id)
    }

    // chat function

    fun checkRoomAvailability(senderId: Long, receiverId: Long): Long {
        var result: Long = 0
        viewModelScope.launch {
            result =  repository.checkRoomAvailability(senderId, receiverId)
        }
        Timber.d(result.toString()+" result room ID")
        return result
    }


    fun createChatRoom(model: RoomModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createChatRoom(model)
        }
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }

    fun getSpecializationName(id: Int): String {
        return repository.getSpecializationName(id)
    }

    fun getPatientCount(doc_id: Long) {
        repository.getPatientCount(doc_id)
    }

    fun getAddress(docId: Long): AddressModel {
        return repository.getAddress(docId)
    }
}