package com.app.consultationpoint.patient.chat.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(private val repository: ChatScreenRepository) : ViewModel() {
    fun sendMsg(msgModel: MessageModel) {
        repository.sendMessage(msgModel)
    }

    fun getStatus(): LiveData<String> {
        return repository.getStatus()
    }

    fun getDoctorDetails(doc_id: Long): UserModel {
        return repository.getDoctorDetails(doc_id)
    }

    fun fetchMsgFromFB(roomId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchMsgFromFB(roomId)
        }
    }

    fun fetchMessages(roomId: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.fetchMessages(roomId)
        }
    }

    fun getMessages(): LiveData<ArrayList<MessageModel>> {
        return repository.getMessages()
    }
}