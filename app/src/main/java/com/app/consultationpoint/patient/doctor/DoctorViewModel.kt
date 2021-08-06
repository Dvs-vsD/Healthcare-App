package com.app.consultationpoint.patient.doctor

import android.content.BroadcastReceiver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel

class DoctorViewModel(private val repository: DoctorRepository): ViewModel() {

    fun init() {
        repository.init()
    }

    fun getDoctorList() : LiveData<ArrayList<UserModel>>{
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
        return repository.checkRoomAvailability(senderId, receiverId)
    }

    fun createChatRoom(model: RoomModel,senderId: Long, receiverId: Long) {
        repository.createChatRoom(model, senderId, receiverId)
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }
}