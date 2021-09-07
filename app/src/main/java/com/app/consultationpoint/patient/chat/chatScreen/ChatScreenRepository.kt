package com.app.consultationpoint.patient.chat.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ChatScreenRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

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

    suspend fun fetchMsgFromFB(roomId: Long) {
        firebaseSource.fetchMessages(roomId)
    }

    suspend fun fetchMessages(roomId: Long) = withContext(Dispatchers.Main) {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Message Fetching instance from realm")

            val mRealmResults =
                mRealm.where(MessageModel::class.java).equalTo("room_id", roomId).findAll()
            val msgList:ArrayList<MessageModel> = mRealm.copyFromRealm(mRealmResults) as ArrayList<MessageModel>
//            msgList.addAll(mRealmResults)
            messageList.value = msgList

            mRealmResults.addChangeListener { change ->
                messageList.value?.clear()
                msgList.clear()
                msgList.addAll(change)
                messageList.postValue(msgList)
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getMessages(): LiveData<ArrayList<MessageModel>> {
        return messageList
    }
}