package com.app.consultationpoint.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.doctor.model.DoctorModel
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
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

        Timber.d("called firebase init")
        val list: ArrayList<UserModel> = ArrayList()
        database.collection("Users").get().addOnSuccessListener {
            for (snapshot in it.documents) {
                Timber.d("user_type %s", snapshot.get("user_type_id").toString())
                if (snapshot.get("user_type_id").toString().toInt() == 1) {
                    val model = UserModel()
                    model.id = snapshot.get("id").toString().toLong()
                    model.first_name = snapshot.get("first_name").toString()
                    model.last_name = snapshot.get("last_name").toString()
                    model.profile = snapshot.get("profile").toString()
                    model.specialization = snapshot.get("specialization").toString()
                    model.city = snapshot.get("city").toString()
                    model.experience_yr = snapshot.get("experience_yr").toString()
                    model.about_info = snapshot.get("about_info").toString()
                    model.mobile = snapshot.get("mobile").toString()
                    model.user_type_id = snapshot.get("user_type_id").toString().toInt()
                    model.user_token = snapshot.get("user_token").toString()
                    model.created_at = snapshot.get("created_at").toString().toLong()
                    model.updated_at = snapshot.get("updated_at").toString().toLong()
                    model.email = snapshot.get("email").toString()
                    model.address = snapshot.get("address").toString()
                    model.doc_id = snapshot.get("doc_id").toString().toLong()

                    list.add(model)
                    Timber.d("list %s", list.toString())
                }
            }

            mRealm.executeTransaction {
                mRealm.insertOrUpdate(list)
            }
        }
    }

    fun fetchMyBookings() {
        val bookingList: ArrayList<AppointmentModel> = ArrayList()
        database.collection("Appointments").get().addOnSuccessListener {
            for (snapshot in it) {
                val model = AppointmentModel()
                model.appointment_id = snapshot.get("appointment_id").toString().toLong()
                model.doctor_id = snapshot.get("doctor_id").toString().toLong()
                model.patient_id = snapshot.get("patient_id").toString().toLong()

                model.schedual_date = snapshot.get("schedual_date").toString()

                model.schedual_time = snapshot.get("schedual_time").toString()
                model.title = snapshot.get("title").toString()
                model.note = snapshot.get("note").toString()
                model.created_at = snapshot.get("created_at").toString().toLong()
                model.created_by = snapshot.get("created_by").toString().toLong()
                model.updated_at = snapshot.get("updated_at").toString().toLong()

                bookingList.add(model)
            }

            mRealm.executeTransaction {
                mRealm.insertOrUpdate(bookingList)
            }
        }
    }

    fun signUp(model: UserModel) {
        firebaseAuth.createUserWithEmailAndPassword(model.email ?: "", model.password ?: "")
            .addOnSuccessListener {
//                val userId = firebaseAuth.currentUser?.uid
                model.id = System.currentTimeMillis()
                model.doc_id = System.currentTimeMillis()
                model.user_token = "" + System.currentTimeMillis()
                model.created_at = System.currentTimeMillis()
                model.updated_at = System.currentTimeMillis()

                database.collection("Users")
                    .document(System.currentTimeMillis().toString()).set(model)
                status.value = "success"

                ConsultationApp.shPref.edit().putString(Const.USER_ID, model.id.toString()).apply()
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

            database.collection("Users").get().addOnSuccessListener {
                for (snapshot in it.documents) {
                    val mail = snapshot.get("email").toString()
                    if (email == mail) {

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_ID, snapshot.get("id").toString()
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.FIRST_NAME, snapshot.get("first_name").toString()
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.LAST_NAME, snapshot.get("last_name").toString()
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_EMAIL, snapshot.get("email").toString()
                        ).apply()

                        ConsultationApp.shPref.edit().putBoolean(Const.PREF_IS_LOGIN, true).apply()

                    }
                }
            }
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
        model.appointment_id = System.currentTimeMillis()
        model.created_at = System.currentTimeMillis()
        model.updated_at = System.currentTimeMillis()

        database.collection("Appointments").document(model.appointment_id.toString()).set(model)
    }

    fun updateProfile(model: UserModel): String {
        var state = ""
        database.collection("Users").document(Utils.getUserId()).set(model).addOnSuccessListener {

            ConsultationApp.shPref.edit().putString(Const.USER_ID, Utils.getUserId()).apply()
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

    fun getDoctorDetails(docId: Long): UserModel {
        val model = UserModel()
        val result = mRealm.where(UserModel::class.java).equalTo("id", docId).findFirst()
        model.first_name = result?.first_name ?: ""
        model.last_name = result?.last_name ?: ""
        model.profile = result?.profile ?: ""
        model.specialization = result?.specialization ?: ""
        model.city = result?.city ?: ""

        return model
    }
}