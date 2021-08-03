package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber

class DoctorRepository(private val firebaseSource: FirebaseSource) {
    private var doctorList: MutableLiveData<ArrayList<UserModel>> = MutableLiveData(ArrayList())
    private lateinit var mRealmResult: RealmResults<UserModel>
    private var mRealm = Realm.getDefaultInstance()

    fun init() {
        firebaseSource.init()

        mRealmResult = mRealm.where(UserModel::class.java).findAll()
        doctorList.value?.addAll(mRealmResult)
        Timber.d("in repo %s", doctorList.value.toString())

        mRealmResult.addChangeListener { change ->
            doctorList.value?.clear()
            val list: ArrayList<UserModel> = ArrayList()
            list.addAll(change)
            doctorList.value = list
            Timber.d("in repo change %s", doctorList.value.toString())
        }
    }

    fun getDoctorList(): LiveData<ArrayList<UserModel>> {
        return doctorList
    }

    fun searchDoctor(str: String) {
        val resultsInFName =
            mRealm.where(UserModel::class.java).contains("first_name", str, Case.INSENSITIVE)
                .findAll()
        val resultsInLName =
            mRealm.where(UserModel::class.java).contains("last_name", str, Case.INSENSITIVE)
                .findAll()
        val set = HashSet<UserModel>()
        set.addAll(resultsInFName)
        set.addAll(resultsInLName)
        if (set.isNotEmpty()) {
            val list: ArrayList<UserModel> = ArrayList()
            list.addAll(set)
            doctorList.value?.clear()
            doctorList.value = list
        }
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

    // chat Functionality

    fun checkRoomAvailability(senderId: Long, receiverId: Long): String {
        val results = mRealm.where(ParticipantModel::class.java).findAll()
        val participantList = ArrayList<ParticipantModel>()
        participantList.addAll(results)

        for (participant in participantList) {
            if ((participant.added_by_id == senderId && participant.user_id == receiverId)
                || (participant.added_by_id == receiverId && participant.user_id == senderId)) {
                return "Record Already There"
            }
        }
        return ""
    }

    fun fetchChatRooms() {
        firebaseSource.fetchChatRooms()
    }

    fun createChatRoom(model: RoomModel,senderId: Long, receiverId: Long) {
        firebaseSource.createChatRoom(model, senderId, receiverId)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getRegistrationStatus()
    }
}