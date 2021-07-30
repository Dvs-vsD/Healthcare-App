package com.app.consultationpoint.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import com.app.consultationpoint.utils.Utils.formatTo
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DashboardRepository(private val firebaseSource: FirebaseSource) {

    private var mRealm: Realm = Realm.getDefaultInstance()

    private lateinit var mRealmResults: RealmResults<AppointmentModel>
    private var todayAptList: MutableLiveData<ArrayList<AppointmentModel>> =
        MutableLiveData(ArrayList())

    fun init() {
        firebaseSource.fetchMyBookings()

        val today = Date().formatTo("yyyy-MM-dd")

        mRealmResults =
            mRealm.where(AppointmentModel::class.java).equalTo("schedual_date", today)
                .findAll()

        todayAptList.value?.addAll(mRealmResults)
    }

    fun fetchTodayApt(today: String) {
        val mRealmResults =
            mRealm.where(AppointmentModel::class.java).equalTo("schedual_date", today).findAll()
        val list: ArrayList<AppointmentModel> = ArrayList()
        list.addAll(mRealmResults)
        Timber.d("list %s", list.toString())
        todayAptList.value?.clear()
        todayAptList.value = list
    }

    fun getTodayAptList(): LiveData<ArrayList<AppointmentModel>> {
        return todayAptList
    }

    fun logout() {
        firebaseSource.logOut()
    }

    fun addDoctorList(list: ArrayList<DoctorModel>) {
        firebaseSource.addDoctorList(list)
    }

    fun isLogin(): Boolean {
        return firebaseSource.isLogin()
    }

    fun getDoctorDetails(docId: Long): UserModel {
        return firebaseSource.getDoctorDetails(docId)
    }
}