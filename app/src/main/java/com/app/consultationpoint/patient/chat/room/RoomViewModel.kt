package com.app.consultationpoint.patient.chat.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomViewModel(private val repository: RoomRepository) : ViewModel() {
    fun getRoomList(): LiveData<ArrayList<RoomModel?>> {
        return repository.getRoomList()
    }

    fun roomsFromRealm(userId: Long) {
        viewModelScope.launch {
            repository.roomsFromRealm(userId)
        }
    }

    fun fetchRoomsFromFirebase(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.roomsFromFirebase(userId)
        }
    }

    fun searchDoctor(str: String) {
        repository.searchDoctor(str)
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }
}