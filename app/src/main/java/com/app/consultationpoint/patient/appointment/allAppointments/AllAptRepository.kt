package com.app.consultationpoint.patient.appointment.allAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AllAptRepository(private val firebaseSource: FirebaseSource) {

    private var eventList: MutableLiveData<ArrayList<AppointmentModel>> =
        MutableLiveData(ArrayList())
    private var oneDayAptList: MutableLiveData<ArrayList<AppointmentModel>> =
        MutableLiveData(ArrayList())
    private var docDetailsList: MutableLiveData<ArrayList<UserModel>> =
        MutableLiveData(ArrayList())

    suspend fun fetchAllEventList() = withContext(Dispatchers.Main) {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults = mRealm.where(AppointmentModel::class.java).findAll()
            var list: ArrayList<AppointmentModel> =
                mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>
            eventList.value = list

            mRealmResults.addChangeListener { change ->
                list.clear()
                eventList.value?.clear()
                list = mRealm.copyFromRealm(change) as ArrayList<AppointmentModel>
                eventList.value = list
            }
        }
    }

    fun getAllEventList(): LiveData<ArrayList<AppointmentModel>> {
        return eventList
    }

    suspend fun getAptForThisDay(date: String) = withContext(Dispatchers.Main) {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults =
                mRealm.where(AppointmentModel::class.java).equalTo("schedual_date", date)
                    .sort("schedual_time", Sort.ASCENDING).findAll()
            val list = mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>

            val docList = ArrayList<UserModel>()
            for (apt in list) {
                val doctorId = apt.doctor_id
                docList.add(getDoctorDetails(doctorId))
            }
            docDetailsList.value = docList

            oneDayAptList.value?.clear()
            oneDayAptList.value = list
        }
    }

    fun getDoctorDetails(docId: Long): UserModel {
        return firebaseSource.getDoctorDetails(docId)
    }

    fun getOneDayAPt(): LiveData<ArrayList<AppointmentModel>> {
        return oneDayAptList
    }

    fun getAptDoctorList(): LiveData<ArrayList<UserModel>> {
        return docDetailsList
    }
}