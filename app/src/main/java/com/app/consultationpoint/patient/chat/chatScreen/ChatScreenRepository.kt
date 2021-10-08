package com.app.consultationpoint.patient.chat.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber
import javax.inject.Inject

class ChatScreenRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private var messageList: MutableLiveData<ArrayList<MessageModel>> = MutableLiveData(ArrayList())
    private var mRealmChatResults: RealmResults<MessageModel>? = null

    fun sendMessage(msgModel: MessageModel) {
        firebaseSource.sendMessage(msgModel)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun getDoctorDetails(id: Long): UserModel {
        return firebaseSource.getUserDetails(id)
    }

    suspend fun fetchMsgFromFB(roomId: Long) {
        firebaseSource.fetchMessages(roomId)
    }

    fun fetchMessages(roomId: Long) {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Message Fetching instance from realm")

            mRealmChatResults =
                mRealm.where(MessageModel::class.java).equalTo("room_id", roomId).findAll()
            var msgList: ArrayList<MessageModel>? =
                mRealm.copyFromRealm(mRealmChatResults) as ArrayList<MessageModel>?
//            msgList.addAll(mRealmResults)
            messageList.value = msgList

            mRealmChatResults?.addChangeListener { change ->
                messageList.value?.clear()
                msgList?.clear()
                msgList = mRealm.copyFromRealm(change) as ArrayList<MessageModel>
                Timber.d("Msg List Updated from Realm")
                messageList.value = msgList
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getMessages(): LiveData<ArrayList<MessageModel>> {
        return messageList
    }
}