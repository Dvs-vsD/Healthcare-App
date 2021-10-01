package com.app.consultationpoint.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.consultationpoint.ConsultationApp
import com.app.consultationpoint.general.model.UserModel
import com.app.consultationpoint.patient.appointment.model.AppointmentModel
import com.app.consultationpoint.patient.chat.chatScreen.model.MessageModel
import com.app.consultationpoint.patient.chat.room.model.ParticipantModel
import com.app.consultationpoint.patient.chat.room.model.RoomModel
import com.app.consultationpoint.patient.dashboard.model.SpecialistModel
import com.app.consultationpoint.patient.userProfile.model.AddressModel
import com.app.consultationpoint.utils.Const
import com.app.consultationpoint.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class FirebaseSource @Inject constructor() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var status: MutableLiveData<String> = MutableLiveData("")

    suspend fun fetchDocFromFB(userType: Int) = withContext(Dispatchers.IO) {
        Timber.d("called firebase init")
        val list: ArrayList<UserModel> = ArrayList()
        database.collection("Users").whereEqualTo("user_type_id", userType).get()
            .addOnSuccessListener {

                for (snapshot in it.documents) {
                    val model = getUser(snapshot)
                    list.add(model)
                }

                Realm.getDefaultInstance().use { mRealm ->
                    Timber.d("User Fetching instance")

                    mRealm.executeTransaction {
                        mRealm.insertOrUpdate(list)
                        status.value = "Doctor List Updated"
                        status.value = ""
                    }
                    Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
                }
            }
    }

    private fun getUser(snapshot: DocumentSnapshot): UserModel {
        val model = UserModel()
        model.id = snapshot.getLong("id") ?: 0 //.toString().toLong()
        if (snapshot.get("doc_id") != null)
            model.doc_id = snapshot.get("doc_id").toString().toLong()
        model.username = snapshot.getString("username") ?: ""
        model.password = snapshot.getString("password") ?: ""
        model.first_name = snapshot.getString("first_name") ?: ""
        model.last_name = snapshot.getString("last_name") ?: ""
        model.email = snapshot.getString("email") ?: ""
        model.mobile = snapshot.getString("mobile") ?: ""
        if (snapshot.get("gender") != null && snapshot.get("gender").toString() != "")
            model.gender = snapshot.getLong("gender")?.toInt() ?: -1
        model.dob = snapshot.getString("dob") ?: ""
        //new added field
        if (snapshot.getString("profile") != null)
            model.profile = snapshot.getString("profile") ?: ""

        model.user_type_id = snapshot.getLong("user_type_id")?.toInt() ?: 0

        database.collection("Addresses").document(model.id.toString()).get()
            .addOnSuccessListener { adr ->
                val adrModel = AddressModel()
                adrModel.user_id = adr.getLong("user_id") ?: 0
                adrModel.address = adr.getString("address") ?: ""
                adrModel.city = adr.getString("city") ?: ""
                adrModel.state = adr.getString("state") ?: ""
                adrModel.country = adr.getString("country") ?: ""
                adrModel.pincode = adr.getLong("pincode")?.toInt() ?: 0
                adrModel.created_at = adr.getLong("created_at") ?: 0
                adrModel.updated_at = adr.getLong("updated_at") ?: 0
                adrModel.to_deleted = adr.getBoolean("to_deleted") ?: false

                Realm.getDefaultInstance().use { mRealm ->
                    mRealm.executeTransaction {
                        mRealm.insertOrUpdate(adrModel)
                    }
                }
            }

        model.user_status = snapshot.getString("user_status") ?: ""
        model.is_deleted = snapshot.getBoolean("is_deleted") ?: false
        model.is_verified = snapshot.getBoolean("is_veryfied") ?: false
        model.created_at = snapshot.getLong("created_at") ?: 0
        model.updated_at = snapshot.getLong("updated_at") ?: 0
        model.user_token = snapshot.getString("user_token") ?: ""
        model.about_info = snapshot.getString("about_info") ?: ""
        model.specialist_id = snapshot.getLong("specialist_id")?.toInt() ?: 0
        model.experience_yr = snapshot.getString("experience_yr") ?: ""
        model.payment_id = snapshot.getString("payment_id") ?: ""
        model.payment_detail = snapshot.getString("payment_detail") ?: ""

        return model
    }

    suspend fun fetchMyBookings() = withContext(Dispatchers.IO) {
        val bookingList: ArrayList<AppointmentModel> = ArrayList()
        val ref = if (Utils.getUserType() == 0)
            database.collection("Appointments")
                .whereEqualTo("patient_id", Utils.getUserId().toLong())
        else
            database.collection("Appointments")
                .whereEqualTo("doctor_id", Utils.getUserId().toLong())

        ref.addSnapshotListener { snapshot, e ->

            var userList: ArrayList<String> = ArrayList()

            if (e != null) {
                Timber.d("Failed to Fetch Rooms")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (results in snapshot.documentChanges) {
                    val model = AppointmentModel()
                    model.appointment_id =
                        results.document.data["appointment_id"].toString().toLong()
                    model.doctor_id = results.document.data["doctor_id"].toString().toLong()
                    model.patient_id = results.document.data["patient_id"].toString().toLong()

                    if (Utils.getUserType() == 1) {
                        userList.add(model.patient_id.toString())
                    }
                    Timber.d("outside condition check")
//                    Thread.sleep(800)

                    model.schedual_date = results.document.data["schedual_date"].toString()

                    model.schedual_time = results.document.data["schedual_time"].toString()
                    model.title = results.document.data["title"].toString()
                    model.note = results.document.data["note"].toString()
                    model.created_at = results.document.data["created_at"].toString().toLong()
                    model.created_by = results.document.data["created_by"].toString().toLong()
                    model.updated_at = results.document.data["updated_at"].toString().toLong()

                    bookingList.add(model)
                }
            }
            Realm.getDefaultInstance().use { mRealm ->
                Timber.d("MyBookings Fetching instance")

                mRealm.executeTransaction {
                    Timber.d("outside data inserted into realm condition check")
                    mRealm.insertOrUpdate(bookingList)
                    if (userList.isNotEmpty()) {
                        fetchDesiredUser(userList, "Apt")
                    } else {
                        status.value = "My Apt Updated"
                        status.value = ""
                        Timber.d("notified without new user condition check")
                    }
                }
                Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
            }
        }
    }

    private fun fetchDesiredUser(list: ArrayList<String>, type: String) {
        for ((index, value) in list.withIndex())
            database.collection("Users").document(value)
                .get().addOnSuccessListener { document ->
                    Timber.d("Inside success listener condition check")
                    val user = getUser(document)
                    Realm.getDefaultInstance().use { mRealm ->
                        mRealm.executeTransaction {
                            mRealm.insertOrUpdate(user)
                            Timber.d("Inside success listener user data inserted condition check $index & ${list.size - 1}")
                            if (index == list.size - 1) {
                                if (type == "Apt") {
                                    status.value = "My Apt Updated"
                                    status.value = ""
                                    Timber.d("notified with new user condition check")
                                } else {
                                    status.value = "Chat Room List Updated"
                                    status.value = ""
                                }
                            }
                        }
                    }
                }
    }

    suspend fun signUp(model: UserModel) = withContext(Dispatchers.Main) {
        firebaseAuth.createUserWithEmailAndPassword(model.email, model.password)
            .addOnSuccessListener {
                model.id = System.currentTimeMillis()
                model.doc_id = System.currentTimeMillis()
                model.user_token = "" + System.currentTimeMillis()
                model.created_at = System.currentTimeMillis()
                model.updated_at = System.currentTimeMillis()

                database.collection("Users")
                    .document(model.id.toString()).set(model)

                ConsultationApp.shPref.edit().putString(Const.USER_ID, model.id.toString()).apply()
                ConsultationApp.shPref.edit().putString(Const.FIRST_NAME, model.first_name).apply()
                ConsultationApp.shPref.edit().putString(Const.LAST_NAME, model.last_name).apply()
                ConsultationApp.shPref.edit().putString(Const.USER_EMAIL, model.email).apply()
                ConsultationApp.shPref.edit().putInt(Const.USER_TYPE, model.user_type_id).apply()
                ConsultationApp.shPref.edit().putBoolean(Const.PREF_IS_LOGIN, true).apply()

                status.value = "success"
                status.value = ""
            }
            .addOnFailureListener {
                status.value = "Error: " + it.message
                status.value = ""
            }
    }

    fun getStatus(): LiveData<String> {
        return status
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.Main) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

            database.collection("Users").get().addOnSuccessListener {

                for (snapshot in it.documents) {
                    val mail = snapshot.getString("email") ?: ""
                    if (email == mail) {

                        val userId = snapshot.get("id").toString()

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_ID, userId
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_NAME, snapshot.getString("username") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.FIRST_NAME, snapshot.getString("first_name") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.LAST_NAME, snapshot.getString("last_name") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_EMAIL, snapshot.getString("email") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putInt(
                            Const.USER_TYPE, snapshot.getLong("user_type_id")?.toInt() ?: 0
                        ).apply()

                        ConsultationApp.shPref.edit().putInt(
                            Const.GENDER, snapshot.getLong("gender")?.toInt() ?: -1
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.PHN_NO, snapshot.getString("mobile") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.DOB, snapshot.getString("dob") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putInt(
                            Const.SPECIALIST_ID, snapshot.getLong("specialist_id")?.toInt() ?: 0
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.EXPERIENCE_YEAR, snapshot.getString("experience_yr") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.ABOUT, snapshot.getString("about_info") ?: ""
                        ).apply()

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_PROFILE, snapshot.getString("profile") ?: ""
                        ).apply()

                        database.collection("Addresses").document(userId).get()
                            .addOnSuccessListener { address ->

                                ConsultationApp.shPref.edit().putString(
                                    Const.ADDRESS, address.getString("address") ?: ""
                                ).apply()

                                ConsultationApp.shPref.edit().putString(
                                    Const.CITY, address.getString("city") ?: ""
                                ).apply()

                                ConsultationApp.shPref.edit().putString(
                                    Const.STATE, address.getString("state") ?: ""
                                ).apply()

                                ConsultationApp.shPref.edit().putString(
                                    Const.COUNTRY, address.getString("country") ?: ""
                                ).apply()

                                ConsultationApp.shPref.edit().putInt(
                                    Const.PIN_CODE, address.getLong("pincode")?.toInt() ?: 0
                                ).apply()
                            }

                        ConsultationApp.shPref.edit().putBoolean(Const.PREF_IS_LOGIN, true).apply()

                        status.value = "login success"
                        status.value = ""
                    }
                }
            }.addOnFailureListener {
                status.value = "Error: User Not Found !!!"
                status.value = ""
            }
        }
            .addOnFailureListener {
                status.value = "Error: " + it.message
                status.value = ""
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        ConsultationApp.shPref.edit().clear().apply()
    }

    fun bookAppointment(model: AppointmentModel) {
        database.collection("Appointments").document(model.appointment_id.toString()).set(model)
            .addOnSuccessListener {
                status.value = "Appointment booked"
                status.value = ""
            }.addOnFailureListener { e ->
                status.value = "Booking Failed: ${e.message}"
                status.value = ""
            }
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel) {
        database.collection("Users").document(Utils.getUserId()).set(model).addOnSuccessListener {

            database.collection("Addresses").document(Utils.getUserId()).set(adrModel)

            ConsultationApp.shPref.edit().putString(Const.USER_ID, model.id.toString()).apply()
            ConsultationApp.shPref.edit().putString(Const.USER_NAME, model.username).apply()
            ConsultationApp.shPref.edit().putString(Const.FIRST_NAME, model.first_name).apply()
            ConsultationApp.shPref.edit().putString(Const.LAST_NAME, model.last_name).apply()
            ConsultationApp.shPref.edit().putString(Const.USER_PROFILE, model.profile).apply()
            ConsultationApp.shPref.edit().putString(Const.PHN_NO, model.mobile).apply()
            ConsultationApp.shPref.edit().putInt(Const.GENDER, model.gender).apply()
            ConsultationApp.shPref.edit().putString(Const.DOB, model.dob).apply()
            ConsultationApp.shPref.edit().putInt(Const.SPECIALIST_ID, model.specialist_id).apply()
            ConsultationApp.shPref.edit().putString(Const.EXPERIENCE_YEAR, model.experience_yr)
                .apply()
            ConsultationApp.shPref.edit().putString(Const.ABOUT, model.about_info).apply()
            ConsultationApp.shPref.edit().putString(Const.ADDRESS, adrModel.address).apply()
            ConsultationApp.shPref.edit().putString(Const.CITY, adrModel.city).apply()
            ConsultationApp.shPref.edit().putString(Const.STATE, adrModel.state).apply()
            ConsultationApp.shPref.edit().putString(Const.COUNTRY, adrModel.country).apply()
            ConsultationApp.shPref.edit().putInt(Const.PIN_CODE, adrModel.pincode).apply()

            status.value = "Profile Updated"
            status.value = ""
        }.addOnFailureListener {
            status.value = "error" + it.message.toString()
            status.value = ""
        }
    }

    fun getUserDetails(docId: Long): UserModel {

        Realm.getDefaultInstance().use { mRealm ->
            var model = UserModel()

            Timber.d("Doctor Details Fetching instance")
            val result = mRealm.where(UserModel::class.java).equalTo("id", docId).findFirst()
            if (result != null)
                model = mRealm.copyFromRealm(result) as UserModel
            Timber.d("Open Instance att %s", System.currentTimeMillis().toString())

            return model
        }
    }

    // chat functionality

    suspend fun fetchChatRooms(userId: Long) = withContext(Dispatchers.IO) {
        Timber.d("coroutine works")
        val start = System.currentTimeMillis()
        database.collection("Rooms")
            .whereArrayContains("user_ids_participants", userId)
            .addSnapshotListener { snapshot, e ->
                val roomList = ArrayList<RoomModel>()

                val userList: ArrayList<String> = ArrayList()

                if (e != null) {
                    Timber.d("Failed to Fetch Rooms")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (results in snapshot.documentChanges) {

                        val room = RoomModel()
                        room.room_id = results.document.data["room_id"].toString().toLong()
                        room.room_type = results.document.data["room_type"].toString().toInt()
                        room.created_by_id =
                            results.document.data["created_by_id"].toString().toLong()
                        room.photo = results.document.data["photo"].toString()
                        room.name = results.document.data["name"].toString()

                        val participantArray: RealmList<Long> = RealmList()
                        val memberList: ArrayList<Map<String, String>> =
                            results.document.data["user_ids_participants"] as ArrayList<Map<String, String>>

                        for (index in 0 until memberList.size) {
                            if (memberList[index].toString() != Utils.getUserId()) {
                                userList.add(memberList[index].toString())
                            }
                            participantArray.add(memberList[index].toString().toLong())
                        }

                        room.user_ids_participants = participantArray

                        val participants: RealmList<ParticipantModel> = RealmList()
                        val participantList: ArrayList<Map<String, String>> =
                            results.document.data["list_participants"] as ArrayList<Map<String, String>>
                        for (participant in participantList) {
                            val mParticipant = ParticipantModel()
                            mParticipant.paticipant_id =
                                participant["paticipant_id"].toString().toLong()
                            mParticipant.room_id = participant["room_id"].toString().toLong()
                            mParticipant.user_id = participant["user_id"].toString().toLong()
                            mParticipant.added_by_id =
                                participant["added_by_id"].toString().toLong()
                            mParticipant.updated_at =
                                participant["updated_at"].toString().toLong()
                            mParticipant.is_deleted =
                                participant["is_deleted"].toString().toBoolean()

                            participants.add(mParticipant)
                        }

                        room.list_participants = participants

                        if (results.document.data["last_message"] != null) {
                            val lastMsg = MessageModel()
                            val lMsgMap: Map<String, String> =
                                results.document.data["last_message"] as Map<String, String>
                            lastMsg.message_id = lMsgMap["message_id"].toString().toLong()
                            lastMsg.room_id = lMsgMap["room_id"].toString().toLong()
                            lastMsg.sender_id = lMsgMap["sender_id"].toString().toLong()
                            lastMsg.content = lMsgMap["content"].toString()
                            lastMsg.content_url = lMsgMap["content_url"].toString()
                            lastMsg.content_type = lMsgMap["content_type"].toString().toInt()
                            lastMsg.status = lMsgMap["status"].toString().toInt()
                            lastMsg.created_at = lMsgMap["created_at"].toString().toLong()
                            lastMsg.updated_at = lMsgMap["updated_at"].toString().toLong()
                            lastMsg.is_deleted = lMsgMap["_deleted"].toString().toBoolean()

                            room.last_message = lastMsg
                        }

                        room.updated_at =
                            results.document.data["updated_at"].toString().toLong()
                        room.created_at =
                            results.document.data["created_at"].toString().toLong()
                        room.is_req_accept_block =
                            results.document.data["_req_accept_block"].toString().toInt()
                        room.is_deleted =
                            results.document.data["_deleted"].toString().toBoolean()

                        roomList.add(room)
                    }
                }

                Realm.getDefaultInstance().use { mRealm ->
                    Timber.d("Chat Room Fetching instance")

                    mRealm.executeTransaction {
                        mRealm.insertOrUpdate(roomList)
                        if (userList.isNotEmpty()) {
                            fetchDesiredUser(userList, "Room")
                        } else {
                            status.value = "Chat Room List Updated"
                            status.value = ""
                            Timber.d("room added to realm %s", roomList.toString())
                        }
                    }
                    Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
                }
                val end = System.currentTimeMillis()
                Timber.d("Time Taken %s", (end - start).toString())
            }
    }

    //Attention: This is checking rooms created only from Patient-------------------

    suspend fun createChatRoom(model: RoomModel) =
        withContext(Dispatchers.IO) {
            /*database.collection("Rooms").whereEqualTo("created_by_id", senderId)
                .whereArrayContains("user_ids_participants", receiverId).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {*/
            database.collection("Rooms").document(model.room_id.toString()).set(model)
                .addOnSuccessListener {
                    status.value = "Room Created Successfully"
                    status.value = ""
                }
                .addOnFailureListener { error ->
                    status.value = "Failed to create chat room: " + error.message
                    status.value = ""
                }
            Timber.d("room added to firebase")
            /*}
        }.addOnFailureListener { e ->
            status.value = e.message
            status.value = ""
        }*/
        }

    fun sendMessage(msgModel: MessageModel) {
        database.collection("Messages").document(msgModel.message_id.toString()).set(msgModel)
            .addOnSuccessListener {
                Timber.d("Msg sent")
                database.collection("Rooms").document(msgModel.room_id.toString())
                    .update("last_message", msgModel)
            }.addOnFailureListener { _ ->
                status.value = "Sending Failed"
                status.value = ""
            }
    }

    suspend fun fetchMessages(roomId: Long) = withContext(Dispatchers.IO) {
        database.collection("Messages").whereEqualTo("room_id", roomId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.d("Listener Failed %s", e.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val msgList = ArrayList<MessageModel>()
                    for (docSnap in snapshot.documentChanges) {
                        val msgModel = MessageModel()
                        msgModel.message_id =
                            docSnap.document.data["message_id"].toString().toLong()
                        msgModel.room_id = docSnap.document.data["room_id"].toString().toLong()
                        msgModel.sender_id =
                            docSnap.document.data["sender_id"].toString().toLong()
                        msgModel.content = docSnap.document.data["content"].toString()
                        msgModel.content_url = docSnap.document.data["content_url"].toString()
                        msgModel.content_type =
                            docSnap.document.data["content_type"].toString().toInt()
                        msgModel.status = docSnap.document.data["status"].toString().toInt()
                        msgModel.created_at =
                            docSnap.document.data["created_at"].toString().toLong()
                        msgModel.updated_at =
                            docSnap.document.data["updated_at"].toString().toLong()
                        msgModel.is_deleted =
                            docSnap.document.data["_deleted"].toString().toBoolean()

                        // msg status list is remaining

                        Timber.d(docSnap.document.data["content"].toString())

                        msgList.add(msgModel)
                    }
                    Realm.getDefaultInstance().use { mRealm ->
                        Timber.d("Message Fetching instance")

                        mRealm.executeTransaction {
                            mRealm.insertOrUpdate(msgList)
                        }
                    }
                    Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
                }
            }
    }

    suspend fun fetchSpFromFB() = withContext(Dispatchers.IO) {
        database.collection("Specialists").get().addOnSuccessListener { document ->
            val list = ArrayList<SpecialistModel>()
            for (doc in document.documents) {
                val model = SpecialistModel()
                model.code = doc.getString("code") ?: ""
                model.color_code = doc.getString("color_code") ?: ""
                model.id = doc.get("id").toString().toInt()
                model.image = doc.getString("image") ?: ""
                model.is_deleted = doc.getBoolean("is_deleted") ?: false
                model.name = doc.getString("name") ?: ""
                model.updated_at = doc.get("updated_at").toString().toLong()

                list.add(model)
            }
            Realm.getDefaultInstance().use { mRealm ->
                mRealm.executeTransaction {
                    it.insertOrUpdate(list)
                    status.value = "Special List Updated"
                    status.value = ""
                }
            }
        }
    }

    fun uploadProfile(path: Uri) {
        val storageRef =
            FirebaseStorage.getInstance().reference
                .child("profile/" + Utils.getUserId() + "_${System.currentTimeMillis()}" + ".jpg")

        storageRef.putFile(path)
            .addOnSuccessListener {
                status.value = "url$storageRef"
                status.value = ""
            }
            .addOnFailureListener {
                status.value = "profile upload failed"
                status.value = ""
            }
    }

    fun getSpecializationName(id: Int): String {
        Realm.getDefaultInstance().use { mRealm ->
            val mRealmResults =
                mRealm.where(SpecialistModel::class.java).equalTo("id", id).findFirst()
            return mRealmResults?.name ?: ""
        }
    }

    fun getPatientCount(docId: Long) {
        database.collection("Appointments").whereEqualTo("doctor_id", docId).get()
            .addOnSuccessListener {
                var count = 0
                for (document in it.documents) {
                    count += 1
                    status.value = "count$count"
                    status.value = ""
                }

            }.addOnFailureListener {
                status.value = "count" + 0
                status.value = ""
            }
    }

    fun searchFromFB(text: String) {
        database.collection("Users").whereEqualTo("user_type_id", 0)
            .whereIn(
                "first_name",
                listOf(text.lowercase(), text.replaceFirstChar(Char::uppercase), text.uppercase())
            )
            /*.orderBy("email")
            .startAt(text)
            .endAt(text + "\uf8ff")*/
            .get()
            .addOnSuccessListener {
                val userList: ArrayList<UserModel> = ArrayList()
                for (snap in it.documents) {
//                    if (snap.getLong("user_type_id") == 0L)
                    userList.add(getUser(snap))
                }
                Realm.getDefaultInstance().use { mRealm ->
                    mRealm.executeTransaction {
                        Timber.d("search doctor list size ${userList.size}")
                        mRealm.insertOrUpdate(userList)
                        status.value = "search found"
                        status.value = ""
                    }
                }
            }.addOnFailureListener {
                Timber.d("search doctor list not found")
                status.value = "search not found"
                status.value = ""
            }
    }

    fun getMyAptCount(patientId: String, doctorId: String) {
        database.collection("Appointments")
            .whereEqualTo("patient_id", patientId)
            .whereEqualTo("doctor_id", doctorId)
            .get()
            .addOnSuccessListener {
                var count = 0
                for (document in it.documents) {
                    count += 1
                }
                status.value = "Apt count$count"
                status.value = ""
            }.addOnFailureListener {
                status.value = "Apt count0"
                status.value = ""
            }
    }
}