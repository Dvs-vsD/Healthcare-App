package com.app.consultationpoint.patient.chat.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel

class ChatScreenViewModel(private val repository: ChatScreenRepository) : ViewModel() {
    fun sendMsg(msgModel: MessageModel) {
        repository.sendMessage(msgModel)
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }

    fun getDoctorDetails(doc_id: Long): UserModel {
        return repository.getDoctorDetails(doc_id)
    }

    fun fetchMessages(roomId: Long){
        repository.fetchMessages(roomId)
    }

    fun getMessages(): LiveData<ArrayList<MessageModel>> {
        return repository.getMessages()
    }
}