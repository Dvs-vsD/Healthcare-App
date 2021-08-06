package com.app.consultationpoint.patient.chat.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel

class RoomViewModel(private val repository: RoomRepository): ViewModel() {
    fun getRoomList(): LiveData<ArrayList<RoomModel?>> {
        return repository.getRoomList()
    }

    fun roomsFromRealm(userId: Long) {
        repository.roomsFromRealm(userId)
    }

    fun fetchRoomsFromFirebase(userId: Long) {
        repository.roomsFromFirebase(userId)
    }

    fun searchDoctor(str: String) {
        repository.searchDoctor(str)
    }

    fun getStatus(): LiveData<String> {
       return repository.getStatus()
    }
}