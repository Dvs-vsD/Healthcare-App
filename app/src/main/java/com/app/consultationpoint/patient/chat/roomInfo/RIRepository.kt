package com.app.consultationpoint.patient.chat.roomInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.utils.Utils
import io.realm.Realm
import io.realm.RealmList
import timber.log.Timber
import javax.inject.Inject

class RIRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private var participantList: MutableLiveData<ArrayList<UserModel>> = MutableLiveData(
        ArrayList()
    )

    fun fetchParticipantFromR(roomId: Long) {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults =
                mRealm.where(RoomModel::class.java).equalTo("room_id", roomId).findFirst()
            val roomModel = mRealm.copyFromRealm(mRealmResults)
            var participantIdList: RealmList<Long>? = roomModel?.user_ids_participants

            Timber.d("Participant list size %s", participantIdList?.size.toString() + "...")

            if (participantIdList != null) {
                fetchUserFromId(participantIdList)
            }

            mRealmResults?.addChangeListener<RoomModel> { change ->
                val model = mRealm.copyFromRealm(change)
                participantIdList?.clear()
                participantIdList = model?.user_ids_participants

                if (participantIdList != null && participantIdList!!.isNotEmpty()) {
                    fetchUserFromId(participantIdList!!)
                }
            }
        }
    }

    private fun fetchUserFromId(list: RealmList<Long>) {

        Timber.d("got list size %s", list.size.toString()+"...")

        Realm.getDefaultInstance().use { mRealm ->
            val pList: ArrayList<UserModel> = ArrayList()

            for (id in list) {
                var participant: UserModel? = null

                if (id.toString() != Utils.getUserId()) {
                    val userData = mRealm.where(UserModel::class.java).equalTo("id", id).findFirst()
                    if (userData != null) {
                        participant = mRealm.copyFromRealm(userData)
                        Timber.d("list other party")
                    }
                } else {
                    val model = UserModel()
                    model.id = Utils.getUserId().toLong()
                    model.username = Utils.getUserName()
                    model.first_name = Utils.getFirstName()
                    model.last_name = Utils.getLastName()
                    model.email = Utils.getUserEmail()
                    model.mobile = Utils.getUserPhnNo()
                    model.gender = Utils.getUserGender().toInt()
                    model.dob = Utils.getDOB()
                    model.profile = Utils.getUserProfile()
                    model.user_type_id = Utils.getUserType()

                    participant = model

                    Timber.d("list of your name")
                }

                if (participant != null) {
                    pList.add(participant)
                    Timber.d("list value Added")
                }
            }
//            participantList.value?.clear()
            participantList.value = pList
        }
    }

    fun getParticipantList(): LiveData<ArrayList<UserModel>> {
        return participantList
    }

    fun getRoomDetails(roomId: Long): RoomModel {
        var room: RoomModel
        Realm.getDefaultInstance().use { mRealm ->
            val roomResult = mRealm.where(RoomModel::class.java).equalTo("room_id", roomId).findFirst()
            room = mRealm.copyFromRealm(roomResult)?: RoomModel()
        }
        return room
    }

    fun getCreatorDetails(createdById: Long): UserModel? {
        var creator: UserModel? = null
        Realm.getDefaultInstance().use { mRealm ->
            val userData = mRealm.where(UserModel::class.java).equalTo("id", createdById).findFirst()
            if (userData != null)
                creator = mRealm.copyFromRealm(userData)
        }
        return creator
    }
}