package com.app.consultationpoint.patient.appointment.myAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.toDate
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MyAptRepository(private val firebaseSource: FirebaseSource) {

    private var appointmentList: MutableLiveData<ArrayList<MonthlyAppointments>> =
        MutableLiveData(ArrayList())
    private var mRealm: Realm = Realm.getDefaultInstance()

    fun fetchAptFromFirebase() {
        firebaseSource.fetchMyBookings()
    }

    fun init() {

        // Apt list not updating with livedata change listener even if realm data changed!!!

        val results =
            mRealm.where(AppointmentModel::class.java)
                .equalTo("patient_id", Utils.getUserId().toLong()).sort("schedual_date").findAll()

        appointmentList.value = monthlyAppointments(getYearMonth(results))

        results.addChangeListener { change ->
            appointmentList.value?.clear()
            Timber.d("Apt in Realm changed")
            appointmentList.value = monthlyAppointments(getYearMonth(change))
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

    private fun monthlyAppointments(sortedList: ArrayList<String>): ArrayList<MonthlyAppointments> {
        val list: ArrayList<MonthlyAppointments> = ArrayList()

        for (month in sortedList) {
            val monthlyModel = MonthlyAppointments()
            monthlyModel.year = month.substring(0, month.length - 3)
            monthlyModel.month = month.substring(5, month.length)

            val mRealmResults =
                mRealm.where(AppointmentModel::class.java)
                    .equalTo("patient_id", Utils.getUserId().toLong())
                    .beginsWith("schedual_date", month)
                    .sort("schedual_date").findAll()

            val aptList: ArrayList<AppointmentModel> = ArrayList()
            for (data in mRealmResults) {
                val model = AppointmentModel()
                model.doctor_id = data.doctor_id
                model.appointment_id = data.appointment_id
                model.patient_id = data.patient_id
                model.schedual_date = data.schedual_date
                model.schedual_time = data.schedual_time
                model.title = data.title
                model.note = data.note

                aptList.add(model)
            }

            monthlyModel.appointment = aptList
            list.add(monthlyModel)
        }
        return list
    }

    fun getMonthlyAptList(): LiveData<ArrayList<MonthlyAppointments>> {
        return appointmentList
    }

    fun getDoctorDetails(doc_id: Long): UserModel {
        return firebaseSource.getDoctorDetails(doc_id)
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
}