package com.app.consultationpoint.patient.chat.roomInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import java.security.cert.LDAPCertStoreParameters

class RIViewModel(private val repository: RIRepository): ViewModel() {
    fun fetchParticipantFromR(roomId: Long) {
        repository.fetchParticipantFromR(roomId)
    }

    fun getParticipantList(): LiveData<ArrayList<UserModel>> {
        return repository.getParticipantList()
    }

    fun getRoomDetails(roomId: Long): RoomModel {
        return repository.getRoomDetails(roomId)
    }

    fun getCreatorDetails(createdById: Long): UserModel? {
        return repository.getCreatorDetails(createdById)
    }
}