package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class DoctorViewModel(private val repository: DoctorRepository) : ViewModel() {

    fun fetchDocFromFB() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchDocFromFB()
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


    fun createChatRoom(model: RoomModel, senderId: Long, receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createChatRoom(model, senderId, receiverId)
        }
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }
}