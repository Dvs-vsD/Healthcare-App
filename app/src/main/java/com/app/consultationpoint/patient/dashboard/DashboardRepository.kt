package com.app.consultationpoint.patient.dashboard

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.toDate
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class DashboardRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private var todayAptList: MutableLiveData<ArrayList<AppointmentModel>> =
        MutableLiveData(ArrayList())
    private var specialItemList: MutableLiveData<ArrayList<SpecialistModel>> =
        MutableLiveData(ArrayList())
    private var docDetailsList: MutableLiveData<ArrayList<UserModel>> =
        MutableLiveData(ArrayList())
    private var aptDateList: MutableLiveData<ArrayList<Calendar>> =
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

    suspend fun fetchAllMyBookings() {
        firebaseSource.fetchMyBookings()
    }

    //    suspend fun fetchTodayApt(day: String) = withContext(Dispatchers.Main) {
    fun fetchTodayApt(day: String) {
        Timber.d(day)
        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Function Today's apt Fetching instance")
            val mRealmResults = if (Utils.getUserType() == 0)
                mRealm.where(AppointmentModel::class.java)
                    .equalTo("patient_id", Utils.getUserId().toLong()).and()
                    .equalTo("schedual_date", day).findAll()
            else
                mRealm.where(AppointmentModel::class.java)
                    .equalTo("doctor_id", Utils.getUserId().toLong()).and()
                    .equalTo("schedual_date", day).findAll()

            val list: ArrayList<AppointmentModel> =
                mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>

            val docList = ArrayList<UserModel>()
            for (apt in list) {
                val doctorId = if (Utils.getUserType() == 0)
                    apt.doctor_id
                else
                    apt.patient_id

                docList.add(getDoctorDetails(doctorId))
            }
            docDetailsList.value = docList

            Timber.d("list %s", list.toString())
            todayAptList.value?.clear()
            todayAptList.value = list

            mRealmResults.addChangeListener { change ->
                todayAptList.value?.clear()
                val newList: ArrayList<AppointmentModel> =
                    mRealm.copyFromRealm(change) as ArrayList<AppointmentModel>

                docList.clear()
                docDetailsList.value?.clear()
                for (apt in newList) {
                    val doctorId = if (Utils.getUserType() == 0)
                        apt.doctor_id
                    else
                        apt.patient_id
                    docList.add(getDoctorDetails(doctorId))
                }
                docDetailsList.value = docList
                todayAptList.value = newList
            }
            Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
        }
    }

    fun getTodayAptList(): LiveData<ArrayList<AppointmentModel>> {
        return todayAptList
    }

    fun getAptDoctorList(): LiveData<ArrayList<UserModel>> {
        return docDetailsList
    }

    fun logout() {
        firebaseSource.logOut()
    }

    fun getDoctorDetails(docId: Long): UserModel {
        return firebaseSource.getUserDetails(docId)
    }

    suspend fun fetchSpItemsFromFB() {
        firebaseSource.fetchSpFromFB()
    }

    suspend fun fetchSpItemsFromRDB() = withContext(Dispatchers.Main) {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults = mRealm.where(SpecialistModel::class.java).findAll()
            var list: ArrayList<SpecialistModel> =
                mRealm.copyFromRealm(mRealmResults) as ArrayList<SpecialistModel>
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

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun fetchAllAptFromRDB() {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults = mRealm.where(AppointmentModel::class.java).findAll()
            var aptList: ArrayList<AppointmentModel> =
                mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>
            aptDateListMaker(aptList)

            mRealmResults.addChangeListener { change ->
                aptList.clear()
                aptList = mRealm.copyFromRealm(change) as ArrayList<AppointmentModel>
                aptDateListMaker(aptList)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun aptDateListMaker(aptList: ArrayList<AppointmentModel>) {
        val list: ArrayList<Calendar> = ArrayList()
        Timber.d(Date().toString())
        for (item in aptList) {
            val aptDate = item.schedual_date.toDate("yyyy-MM-dd")
            val time = item.schedual_time

            if (aptDate != null) {

                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, aptDate.formatTo("yyyy").toInt())
                cal.set(Calendar.MONTH, aptDate.formatTo("MM").toInt() - 1)
                cal.set(Calendar.DATE, aptDate.formatTo("dd").toInt())

                var hour = time.substring(0, 2)
                if (hour == "12")
                    hour = "0"
                val minute = time.substring(3, 5)
                val am_pm = time.substring(6, time.length)
                val phase: Int = if (am_pm == "AM") {
                    0
                } else {
                    1
                }

                cal.set(Calendar.HOUR, hour.toInt())
                cal.set(Calendar.MINUTE, minute.toInt())
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.set(Calendar.AM_PM, phase)

                val date = cal.time
                val today = Date()

                if (date >= today) {
                    list.add(cal)
                }
            }
        }
        list.sort()
        aptDateList.value = list
    }

    fun getAptDateList(): LiveData<ArrayList<Calendar>> {
        return aptDateList
    }

    suspend fun fetchDoctorsFromFB(type_id: Int) {
        firebaseSource.fetchDocFromFB(type_id)
    }

    fun getSpecializationName(specialistId: Int): String {
        return firebaseSource.getSpecializationName(specialistId)
    }
}