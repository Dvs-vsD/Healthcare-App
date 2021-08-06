package com.app.consultationpoint.patient.chat.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import io.realm.Realm

class ChatScreenRepository(private val firebaseSource: FirebaseSource) {

    private var mRealm = Realm.getDefaultInstance()
    private var messageList: MutableLiveData<ArrayList<MessageModel>> = MutableLiveData(ArrayList())

    fun sendMessage(msgModel: MessageModel) {
        firebaseSource.sendMessage(msgModel)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun getDoctorDetails(id: Long): UserModel {
        val model = UserModel()
        val result = mRealm.where(UserModel::class.java).equalTo("id", id).findFirst()
        model.first_name = result?.first_name ?: ""
        model.last_name = result?.last_name ?: ""
        model.specialization = result?.specialization ?: ""
        model.city = result?.city ?: ""
        model.profile = result?.profile ?: ""

        return model
    }

    fun fetchMessages(roomId: Long) {
        firebaseSource.fetchMessages(roomId)

        val mRealmResults = mRealm.where(MessageModel::class.java).equalTo("room_id", roomId).findAll()
        val msgList = ArrayList<MessageModel>()
        msgList.addAll(mRealmResults)
        messageList.value = msgList

        mRealmResults.addChangeListener { change ->
            messageList.value?.clear()
            msgList.clear()
            msgList.addAll(change)
            messageList.value = msgList
        }
    }

    fun getMessages(): LiveData<ArrayList<MessageModel>> {
        return messageList
    }
}