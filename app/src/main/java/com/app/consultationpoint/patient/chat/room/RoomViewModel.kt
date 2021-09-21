package com.app.consultationpoint.patient.chat.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(private val repository: RoomRepository) : ViewModel() {
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

    fun getUserDetails(userId: Long): UserModel {
        return repository.getUserDetails(userId)
    }
}