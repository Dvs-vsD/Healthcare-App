package com.app.consultationpoint.patient.doctor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.utils.Utils
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class DoctorRepository @Inject constructor(private val firebaseSource: FirebaseSource) {
    private var doctorList: MutableLiveData<ArrayList<UserModel>> = MutableLiveData(ArrayList())

    suspend fun fetchDocFromFB(userType: Int) {
        firebaseSource.fetchDocFromFB(userType)
    }

    suspend fun fetchDocFromRDB() = withContext(Dispatchers.Main) {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Doctor list Fetching instance")

            val usertype = if (Utils.getUserType() == 0)
                1
            else
                0

            val mRealmResult =
                mRealm.where(UserModel::class.java).equalTo("user_type_id", usertype).findAll()
            val dList: ArrayList<UserModel> =
                mRealm.copyFromRealm(mRealmResult) as ArrayList<UserModel>
            doctorList.value = dList
            Timber.d("in repo %s", doctorList.value.toString())

            mRealmResult.addChangeListener { change ->
                doctorList.value?.clear()
                val list: ArrayList<UserModel> = ArrayList()
                list.addAll(change)
                doctorList.value = list
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

            val usertype = if (Utils.getUserType() == 0)
                1
            else
                0

            val results =
                mRealm.where(UserModel::class.java).equalTo("user_type_id", usertype).and().contains("first_name", str, Case.INSENSITIVE)
                    .or().equalTo("user_type_id", usertype).and().contains("last_name", str, Case.INSENSITIVE).findAll()
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
        return firebaseSource.getUserDetails(id)
    }

    // chat Functionality

    suspend fun checkRoomAvailability(senderId: Long, receiverId: Long): Long =
        withContext(Dispatchers.Main) {
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

            return@withContext participant?.room_id ?: 0
        }

    suspend fun createChatRoom(model: RoomModel) {
        firebaseSource.createChatRoom(model)
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun getSpecializationName(id: Int): String {
        return firebaseSource.getSpecializationName(id)
    }

    fun getPatientCount(docId: Long) {
        firebaseSource.getPatientCount(docId)
    }

    fun getAddress(docId: Long): AddressModel {

        Realm.getDefaultInstance().use { mRealm ->
            var model = AddressModel()

            val mRealmResult = mRealm.where(AddressModel::class.java).equalTo("user_id", docId).findFirst()
            if (mRealmResult != null) {
                model = mRealm.copyFromRealm(mRealmResult) as AddressModel
            }
            return model
        }
    }

    fun searchFromFB(text: String) {
        firebaseSource.searchFromFB(text)
    }

    fun getMyAptCount(patientId: Long, doctorId: Long) {
        firebaseSource.getMyAptCount(patientId, doctorId)
    }
}