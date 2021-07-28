package com.app.consultationpoint.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import timber.log.Timber

class FirebaseSource {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var status: MutableLiveData<String> = MutableLiveData("")
    private val mRealm: Realm = Realm.getDefaultInstance()

    fun init() {

        Timber.d("called firebase")
        val list: ArrayList<DoctorModel> = ArrayList()
        database.collection("Doctor").get().addOnSuccessListener {
            for (snapshot in it) {
                val model = DoctorModel()
                model.doc_id = snapshot.get("doc_id").toString()
                model.first_name = snapshot.get("first_name").toString()
                model.last_name = snapshot.get("last_name").toString()
                model.profile = snapshot.get("profile").toString()
                model.specialization = snapshot.get("specialization").toString()
                model.city = snapshot.get("city").toString()

                list.add(model)
            }

            mRealm.executeTransaction {
                mRealm.insertOrUpdate(list)
            }
        }

        fetchMyBookings()
    }

    fun fetchMyBookings() {
        val bookingList: ArrayList<AppointmentModel> = ArrayList()
        database.collection("User").document(Utils.getUserId())
            .collection("My Bookings").get().addOnSuccessListener {
                for (snapshot in it) {
                    val model = AppointmentModel()
                    model.booking_id = snapshot.id
                    model.doc_id = snapshot.get("doc_id").toString()
                    model.patient_id = snapshot.get("patient_id").toString()

                    val timestamp: Timestamp = snapshot.get("schedual_date") as Timestamp
                    val yearMonth = timestamp.toDate().formatTo("yyyy-MM")
                    model.year_month = yearMonth
                    model.schedual_date = timestamp.toDate()

                    model.schedual_time = snapshot.get("schedual_time").toString()
                    model.appointmentTitle = snapshot.get("appointmentTitle").toString()
                    model.appointmentDesc = snapshot.get("appointmentDesc").toString()

                    bookingList.add(model)
                }

                mRealm.executeTransaction {
                    mRealm.insertOrUpdate(bookingList)
                }
            }
    }

    fun patientSignUp(model: UserModel) {
        firebaseAuth.createUserWithEmailAndPassword(model.email?:"", model.password?:"").addOnSuccessListener {
            val userId = firebaseAuth.currentUser?.uid
            model.user_id = userId ?: ""

            database.collection("Users")
                .document(userId ?: "${System.currentTimeMillis()}").set(model)
            status.value = "success"

            ConsultationApp.shPref.edit().putString(Const.USER_ID, userId).apply()
            ConsultationApp.shPref.edit().putString(Const.FIRST_NAME, model.first_name).apply()
            ConsultationApp.shPref.edit().putString(Const.LAST_NAME, model.last_name).apply()
            ConsultationApp.shPref.edit().putString(Const.USER_EMAIL, model.email).apply()
            ConsultationApp.shPref.edit().putBoolean(Const.PREF_IS_LOGIN, true).apply()
        }
            .addOnFailureListener {
                status.value = it.message
            }
    }

    fun getRegistrationStatus(): LiveData<String> {
        return status
    }

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            status.value = "success"
        }
            .addOnFailureListener {
                status.value = it.message
            }
    }

    fun isLogin(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logOut() {
        firebaseAuth.signOut()
        status.value = ""
        ConsultationApp.shPref.edit().putBoolean(Const.PREF_IS_LOGIN, false).apply()
    }

    fun addDoctorList(list: ArrayList<DoctorModel>) {
        for (index in 0 until list.size) {
            database.collection("Doctor").document(index.toString()).set(list[index])
        }
    }

    fun bookAppointment(model: AppointmentModel) {
        database.collection("User").document(model.patient_id)
            .collection("My Bookings").document().set(model)

        fetchMyBookings()
    }

    fun updateProfile(model: UserModel): String {
        var state = ""
        database.collection("User").document(model.user_id?:"").set(model).addOnSuccessListener {

            ConsultationApp.shPref.edit().putString(Const.USER_ID, model.user_id).apply()
            ConsultationApp.shPref.edit().putString(Const.FIRST_NAME, model.first_name).apply()
            ConsultationApp.shPref.edit().putString(Const.LAST_NAME, model.last_name).apply()
            ConsultationApp.shPref.edit().putString(Const.USER_PROFILE, model.profile).apply()
            ConsultationApp.shPref.edit().putString(Const.PHN_NO, model.mobile).apply()
            ConsultationApp.shPref.edit().putString(Const.ADDRESS, model.address).apply()

            state = "success"
        }.addOnFailureListener {
            state = it.message.toString()
        }
        return state
    }

    fun getDoctorDetails(docId: String): DoctorModel {
        val model = DoctorModel()
        val result = mRealm.where(DoctorModel::class.java).equalTo("doc_id", docId).findFirst()
        model.first_name = result?.first_name ?: ""
        model.last_name = result?.last_name ?: ""
        model.profile = result?.profile ?: ""
        model.specialization = result?.specialization ?: ""
        model.city = result?.city ?: ""

        return model
    }
}