package com.app.consultationpoint.patient.chat.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import io.realm.Realm
import timber.log.Timber

class ChatScreenRepository(private val firebaseSource: FirebaseSource) {

    //    private var mRealm = Realm.getDefaultInstance()
    private var messageList: MutableLiveData<ArrayList<MessageModel>> = MutableLiveData(ArrayList())

    fun sendMessage(msgModel: MessageModel) {
        firebaseSource.sendMessage(msgModel)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun getDoctorDetails(id: Long): UserModel {
        return firebaseSource.getDoctorDetails(id)
    }

    fun fetchMessages(roomId: Long) {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Message Fetching instance from realm")
            firebaseSource.fetchMessages(roomId)

            val mRealmResults =
                mRealm.where(MessageModel::class.java).equalTo("room_id", roomId).findAll()
            val msgList:ArrayList<MessageModel> = mRealm.copyFromRealm(mRealmResults) as ArrayList<MessageModel>
//            msgList.addAll(mRealmResults)
            messageList.value = msgList

            mRealmResults.addChangeListener { change ->
                messageList.value?.clear()
                msgList.clear()
                msgList.addAll(change)
                messageList.value = msgList
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getMessages(): LiveData<ArrayList<MessageModel>> {
        return messageList
    }
}