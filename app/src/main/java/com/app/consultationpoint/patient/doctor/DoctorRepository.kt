package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import io.realm.Case
import io.realm.Realm
import timber.log.Timber

class DoctorRepository(private val firebaseSource: FirebaseSource) {
    private var doctorList: MutableLiveData<ArrayList<UserModel>> = MutableLiveData(ArrayList())

    fun fetchDocFromFB() {
        firebaseSource.fetchDocFromFB()
    }

    fun fetchDocFromRDB() {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Doctor list Fetching instance")
            val usertype = 1

            val mRealmResult =
                mRealm.where(UserModel::class.java).equalTo("user_type_id", usertype).findAll()
            val dList: ArrayList<UserModel> =
                mRealm.copyFromRealm(mRealmResult) as ArrayList<UserModel>
            doctorList.postValue(dList)
            Timber.d("in repo %s", doctorList.value.toString())

            mRealmResult.addChangeListener { change ->
                doctorList.value?.clear()
                val list: ArrayList<UserModel> = ArrayList()
                list.addAll(change)
                doctorList.postValue(list)
                Timber.d("in repo change %s", doctorList.value.toString())
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getDoctorList(): MutableLiveData<ArrayList<UserModel>> {
        return doctorList
    }

    fun searchDoctor(str: String) {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("search doctor (in doctor repo) Fetching instance")

            val results =
                mRealm.where(UserModel::class.java).contains("first_name", str, Case.INSENSITIVE)
                    .or().contains("last_name", str, Case.INSENSITIVE).findAll()
            if (results.isNotEmpty()) {
                val list: ArrayList<UserModel> = ArrayList()
                list.addAll(results)
                doctorList.value?.clear()
                doctorList.value = list
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getDoctorDetails(id: Long): UserModel {
        return firebaseSource.getDoctorDetails(id)
    }

    // chat Functionality

    fun checkRoomAvailability(senderId: Long, receiverId: Long): Long {
        var participant: ParticipantModel? = null

        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Check room availability instance")
            val results = mRealm.where(ParticipantModel::class.java)
                .equalTo("added_by_id", senderId).and().equalTo("user_id", receiverId)
                .or()
                .equalTo("added_by_id", receiverId).and().equalTo("user_id", senderId)
                .findFirst()
            if (results != null)
                participant = mRealm.copyFromRealm(results)
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }

        return participant?.room_id ?: 0
    }

    fun createChatRoom(model: RoomModel, senderId: Long, receiverId: Long) {
        firebaseSource.createChatRoom(model, senderId, receiverId)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }
}