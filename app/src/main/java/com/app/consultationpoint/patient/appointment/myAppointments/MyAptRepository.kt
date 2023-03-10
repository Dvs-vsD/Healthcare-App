package com.app.consultationpoint.patient.appointment.myAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.toDate
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MyAptRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private var appointmentList: MutableLiveData<ArrayList<MonthlyAppointments>> =
        MutableLiveData(ArrayList())
    private var mRealm: Realm = Realm.getDefaultInstance()

    suspend fun fetchAptFromFirebase() {
        firebaseSource.fetchMyBookings()
    }

    suspend fun fetchAptFromRealm() = withContext(Dispatchers.Main) {
        val results = if (Utils.getUserType() == 0)
            mRealm.where(AppointmentModel::class.java)
                .equalTo("patient_id", Utils.getUserId().toLong()).sort("schedual_date").findAll()
        else
            mRealm.where(AppointmentModel::class.java)
                .equalTo("doctor_id", Utils.getUserId().toLong()).sort("schedual_date").findAll()

        appointmentList.value = monthlyAppointments(getYearMonth(results), false, 0)

        results.addChangeListener { change ->
            appointmentList.value?.clear()
            Timber.d("Apt in Realm changed")
            appointmentList.value = monthlyAppointments(getYearMonth(change), false, 0)
        }
    }

    private fun getYearMonth(results: RealmResults<AppointmentModel>): ArrayList<String> {
        val set = HashSet<String>()

        for (result in results) {
            val date = result.schedual_date
            set.add(date.substring(0, date.length - 3))
        }

        val sortedList: ArrayList<String> = ArrayList(set)
        sortedList.sort()

        return sortedList
    }

    private fun monthlyAppointments(
        sortedList: ArrayList<String>,
        specificUser: Boolean,
        spUserId: Long
    ): ArrayList<MonthlyAppointments> {
        val list: ArrayList<MonthlyAppointments> = ArrayList()

        for (month in sortedList) {
            val monthlyModel = MonthlyAppointments()
            monthlyModel.year = month.substring(0, month.length - 3)
            monthlyModel.month = month.substring(5, month.length)

            val mRealmResults = if (Utils.getUserType() == 0) {
                if (specificUser)
                    mRealm.where(AppointmentModel::class.java)
                        .equalTo("patient_id", Utils.getUserId().toLong())
                        .and()
                        .equalTo("doctor_id", spUserId)
                        .beginsWith("schedual_date", month)
                        .sort("schedual_date").findAll()
                else
                    mRealm.where(AppointmentModel::class.java)
                        .equalTo("patient_id", Utils.getUserId().toLong())
                        .beginsWith("schedual_date", month)
                        .sort("schedual_date").findAll()
            } else {
                if (specificUser)
                    mRealm.where(AppointmentModel::class.java)
                        .equalTo("doctor_id", Utils.getUserId().toLong())
                        .and()
                        .equalTo("patient_id", spUserId)
                        .beginsWith("schedual_date", month)
                        .sort("schedual_date").findAll()
                else
                    mRealm.where(AppointmentModel::class.java)
                        .equalTo("doctor_id", Utils.getUserId().toLong())
                        .beginsWith("schedual_date", month)
                        .sort("schedual_date").findAll()
            }

            val aptList: ArrayList<AppointmentModel> =
                mRealm.copyFromRealm(mRealmResults) as ArrayList<AppointmentModel>
            monthlyModel.appointment = aptList
            list.add(monthlyModel)
        }
        return list
    }

    fun getMonthlyAptList(): LiveData<ArrayList<MonthlyAppointments>> {
        return appointmentList
    }

    fun getDoctorDetails(doc_id: Long): UserModel {
        return firebaseSource.getUserDetails(doc_id)
    }

    private fun currentAndNextMonth(result: AppointmentModel, dateType: String): Date {
        val cal = Calendar.getInstance()
        cal.time = result.schedual_date.toDate() ?: Date()
        if (dateType == "start") {
            cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        } else {
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        Timber.d("date %s", cal.time)

        return cal.time
    }

    fun getStatus(): LiveData<String> {
        return firebaseSource.getStatus()
    }

    fun fYourAptWithSpecificUser(spUserId: Long) {
        val results = if (Utils.getUserType() == 0)
            mRealm.where(AppointmentModel::class.java)
                .equalTo("patient_id", Utils.getUserId().toLong())
                .and()
                .equalTo("doctor_id", spUserId)
                .sort("schedual_date").findAll()
        else
            mRealm.where(AppointmentModel::class.java)
                .equalTo("doctor_id", Utils.getUserId().toLong())
                .and()
                .equalTo("patient_id", spUserId)
                .sort("schedual_date").findAll()

        appointmentList.value = monthlyAppointments(getYearMonth(results), true, spUserId)

        results.addChangeListener { change ->
            appointmentList.value?.clear()
            Timber.d("Apt in Realm changed")
            appointmentList.value = monthlyAppointments(getYearMonth(change), true, spUserId)
        }
    }
}