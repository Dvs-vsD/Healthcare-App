package com.app.consultationpoint.patient.chat.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import io.realm.Case
import io.realm.Realm
import timber.log.Timber

class RoomRepository(private val firebaseSource: FirebaseSource) {

    private val mRealm: Realm = Realm.getDefaultInstance()
    private val roomList: MutableLiveData<ArrayList<RoomModel?>> = MutableLiveData(ArrayList())

    fun getRoomList(): LiveData<ArrayList<RoomModel?>> {
        return roomList
    }

    fun roomsFromFirebase(userId: Long) {
        firebaseSource.fetchChatRooms(userId)
    }

    fun roomsFromRealm(userId: Long) {

        val list = ArrayList<RoomModel?>()
        val mRealmResults =
            mRealm.where(RoomModel::class.java).equalTo("list_participants.user_id", userId)
                .findAll()
        list.addAll(mRealmResults)
        roomList.value = list

        mRealmResults.addChangeListener { change ->
            roomList.value?.clear()
            list.clear()
            list.addAll(change)
            roomList.value = list
        }
        Timber.d("Fetched from realm")
    }

    fun searchDoctor(str: String) {
        val mRealmResults = mRealm.where(RoomModel::class.java)
            .equalTo("created_by_id", Utils.getUserId().toLong()).and()
            .contains("name", str, Case.INSENSITIVE).findAll()
        val list = ArrayList<RoomModel?>()
        roomList.value?.clear()
        list.addAll(mRealmResults)
        roomList.value = list
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }
}