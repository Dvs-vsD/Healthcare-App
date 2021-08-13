package com.app.consultationpoint.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DashboardRepository(private val firebaseSource: FirebaseSource) {

//    private var mRealm: Realm = Realm.getDefaultInstance()

//    private lateinit var mRealmResults: RealmResults<AppointmentModel>
    private var todayAptList: MutableLiveData<ArrayList<AppointmentModel>> =
        MutableLiveData(ArrayList())
    private var specialItemList: MutableLiveData<ArrayList<SpecialistModel>> =
        MutableLiveData(ArrayList())

//    fun init() {
//        firebaseSource.fetchMyBookings()
//
//        Realm.getDefaultInstance().use { mRealm ->
//            Timber.d("initial day Apt Fetching instance")
//            val today = Date().formatTo("yyyy-MM-dd")
//
//            mRealmResults =
//                mRealm.where(AppointmentModel::class.java).equalTo("schedual_date", today)
//                    .findAll()
//            val aptList: ArrayList<AppointmentModel> = mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>
//            todayAptList.value = aptList
//            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
//        }
//    }

    fun fetchTodayApt(today: String) {
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Function Today's apt Fetching instance")
            val mRealmResults =
                mRealm.where(AppointmentModel::class.java)
                    .equalTo("patient_id", Utils.getUserId().toLong()).and()
                    .equalTo("schedual_date", today).findAll()
            val list: ArrayList<AppointmentModel> = mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>
            Timber.d("list %s", list.toString())
            todayAptList.value?.clear()
            todayAptList.value = list

            mRealmResults.addChangeListener { change ->
                todayAptList.value?.clear()
                val newList: ArrayList<AppointmentModel> = mRealm.copyFromRealm(change) as ArrayList<AppointmentModel>
                todayAptList.value = newList
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getTodayAptList(): LiveData<ArrayList<AppointmentModel>> {
        return todayAptList
    }

    fun logout() {
        firebaseSource.logOut()
    }

    fun getDoctorDetails(docId: Long): UserModel {
        return firebaseSource.getDoctorDetails(docId)
    }

    fun fetchSpItemsFromFB() {
        firebaseSource.fetchSpFromFB()
    }

    fun fetchSpItemsFromRDB() {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults = mRealm.where(SpecialistModel::class.java).findAll()
            var list: ArrayList<SpecialistModel> = mRealm.copyFromRealm(mRealmResults) as ArrayList<SpecialistModel>
            specialItemList.value = list

            mRealmResults.addChangeListener { change ->
                list.clear()
                specialItemList.value?.clear()
                list = mRealm.copyFromRealm(change) as ArrayList<SpecialistModel>
                specialItemList.value = list
            }
        }
    }

    fun getSpCategoryList(): LiveData<ArrayList<SpecialistModel>> {
        return specialItemList
    }
}