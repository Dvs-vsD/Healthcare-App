package com.app.consultationpoint.patient.appointment.myAppointments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.firebase.FirebaseSource
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.appointment.model.MonthlyAppointments
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import com.app.consultationpoint.utils.Utils.formatTo
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MyAptRepository(private val firebaseSource: FirebaseSource) {

    private var appointmentList: MutableLiveData<ArrayList<MonthlyAppointments>> =
        MutableLiveData(ArrayList())
    private var mRealm: Realm = Realm.getDefaultInstance()

    fun init() {
        val results =
            mRealm.where(AppointmentModel::class.java).distinct("year_month")
                .sort("year_month").findAll()

        appointmentList.value = monthlyAppointments(results)

        results.addChangeListener { change ->
            appointmentList.value = monthlyAppointments(change)
        }
    }

    private fun monthlyAppointments(results: RealmResults<AppointmentModel>): ArrayList<MonthlyAppointments> {
        val list: ArrayList<MonthlyAppointments> = ArrayList()

        for (result in results) {
            val monthlyModel = MonthlyAppointments()
            monthlyModel.year = result.schedual_date?.formatTo("yyyy") ?: ""
            monthlyModel.month = result.schedual_date?.formatTo("MMMM") ?: ""

            val startDate = currentAndNextMonth(result, "start")
            val endDate = currentAndNextMonth(result, "end")

//            val fields = arrayOf("schedual_time","schedual_date")
//            val sortOrders = arrayOf(Sort.ASCENDING,Sort.ASCENDING)

            val mRealmResults = mRealm.where(AppointmentModel::class.java)
                .between("schedual_date", startDate, endDate).sort("schedual_date").findAll()

            val aptList: ArrayList<AppointmentModel> = ArrayList()
            for (data in mRealmResults) {
                val model = AppointmentModel()
                model.doc_id = data.doc_id
                model.booking_id = data.booking_id
                model.patient_id = data.patient_id
                model.schedual_date = data.schedual_date
                model.schedual_time = data.schedual_time
                model.appointmentTitle = data.appointmentTitle
                model.appointmentDesc = data.appointmentDesc

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

    fun getDoctorDetails(doc_id: String): DoctorModel {
        return firebaseSource.getDoctorDetails(doc_id)
    }

    private fun currentAndNextMonth(result: AppointmentModel, dateType: String): Date {
        val cal = Calendar.getInstance()
        cal.time = result.schedual_date ?: Date()
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