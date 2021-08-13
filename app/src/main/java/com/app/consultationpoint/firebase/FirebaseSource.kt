package com.app.consultationpoint.firebase

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
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import io.realm.RealmList
import timber.log.Timber

class FirebaseSource {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var status: MutableLiveData<String> = MutableLiveData("")
//    private val mRealm: Realm = Realm.getDefaultInstance()

    fun fetchDocFromFB() {
        Timber.d("called firebase init")
        val list: ArrayList<UserModel> = ArrayList()
        database.collection("Users").whereEqualTo("user_type_id", 1).get().addOnSuccessListener {

            for (snapshot in it.documents) {
                val model = UserModel()
                model.id = snapshot.get("id").toString().toLong()
                model.doc_id = snapshot.get("doc_id").toString().toLong()
                model.username = snapshot.get("username").toString()
                model.password = snapshot.get("password").toString()
                model.first_name = snapshot.get("first_name").toString()
                model.last_name = snapshot.get("last_name").toString()
                model.email = snapshot.get("email").toString()
                model.mobile = snapshot.get("mobile").toString()
                if (snapshot.get("gender") != null && snapshot.get("gender").toString() != "")
                    model.gender = snapshot.get("gender").toString().toInt()
                model.dob = snapshot.get("dob").toString()
                //new added field
                model.profile = snapshot.get("profile").toString()

                model.user_type_id = snapshot.get("user_type_id").toString().toInt()
                model.user_status = snapshot.get("user_status").toString()
                model.is_deleted = snapshot.get("is_deleted").toString().toBoolean()
                model.is_verified = snapshot.get("is_veryfied").toString().toBoolean()
                model.created_at = snapshot.get("created_at").toString().toLong()
                model.updated_at = snapshot.get("updated_at").toString().toLong()
                model.user_token = snapshot.get("user_token").toString()
                model.about_info = snapshot.get("about_info").toString()
                model.specialist_id = snapshot.get("specialist_id").toString()
                model.experience_yr = snapshot.get("experience_yr").toString()
                model.payment_id = snapshot.get("payment_id").toString()
                model.payment_detail = snapshot.get("payment_detail").toString()

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

    fun fetchMyBookings() {
        val bookingList: ArrayList<AppointmentModel> = ArrayList()
        database.collection("Appointments").whereEqualTo("patient_id", Utils.getUserId().toLong())
            .get().addOnSuccessListener {

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
                Realm.getDefaultInstance().use { mRealm ->
                    Timber.d("MyBookings Fetching instance")

                    mRealm.executeTransaction {
                        mRealm.insertOrUpdate(bookingList)
                    }
                    Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
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
                status.value = it.message
                status.value = ""
            }
    }

    fun getStatus(): LiveData<String> {
        return status
    }

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

            database.collection("Users").get().addOnSuccessListener {

                for (snapshot in it.documents) {
                    val mail = snapshot.get("email").toString()
                    if (email == mail) {

                        val userId = snapshot.get("id").toString()

                        ConsultationApp.shPref.edit().putString(
                            Const.USER_ID, userId
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

                        ConsultationApp.shPref.edit().putInt(
                            Const.USER_TYPE, snapshot.get("user_type_id").toString().toInt()
                        ).apply()

                        database.collection("Addresses").document(userId).get()
                            .addOnSuccessListener { address ->

                                ConsultationApp.shPref.edit().putString(
                                    Const.ADDRESS, address.get("address").toString()
                                ).apply()

                                ConsultationApp.shPref.edit().putString(
                                    Const.CITY, address.get("city").toString()
                                ).apply()

                                ConsultationApp.shPref.edit().putString(
                                    Const.STATE, address.get("state").toString()
                                ).apply()

                                ConsultationApp.shPref.edit().putString(
                                    Const.COUNTRY, address.get("country").toString()
                                ).apply()

                                ConsultationApp.shPref.edit().putInt(
                                    Const.PIN_CODE, address.get("pincode").toString().toInt()
                                ).apply()
                            }

                        ConsultationApp.shPref.edit().putBoolean(Const.PREF_IS_LOGIN, true).apply()
                    }
                }

                status.value = "success"
                status.value = ""
            }
        }
            .addOnFailureListener {
                status.value = it.message
                status.value = ""
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        status.value = ""
        ConsultationApp.shPref.edit().clear().apply()
    }

    fun bookAppointment(model: AppointmentModel) {
        model.appointment_id = System.currentTimeMillis()
        model.created_at = System.currentTimeMillis()
        model.updated_at = System.currentTimeMillis()

        database.collection("Appointments").document(model.appointment_id.toString()).set(model)
    }

    fun updateProfile(model: UserModel, adrModel: AddressModel) {
        database.collection("Users").document(Utils.getUserId()).set(model).addOnSuccessListener {

            database.collection("Addresses").document(Utils.getUserId()).set(adrModel)

            ConsultationApp.shPref.edit().putString(Const.USER_ID, Utils.getUserId()).apply()
            ConsultationApp.shPref.edit().putString(Const.FIRST_NAME, model.first_name).apply()
            ConsultationApp.shPref.edit().putString(Const.LAST_NAME, model.last_name).apply()
            ConsultationApp.shPref.edit().putString(Const.USER_PROFILE, model.profile).apply()
            ConsultationApp.shPref.edit().putString(Const.PHN_NO, model.mobile).apply()
            ConsultationApp.shPref.edit().putString(Const.ADDRESS, adrModel.address).apply()
            ConsultationApp.shPref.edit().putString(Const.CITY, adrModel.city).apply()
            ConsultationApp.shPref.edit().putString(Const.STATE, adrModel.state).apply()
            ConsultationApp.shPref.edit().putString(Const.COUNTRY, adrModel.country).apply()
            ConsultationApp.shPref.edit().putInt(Const.PIN_CODE, adrModel.pincode).apply()

            status.value = "Profile Updated"
            status.value = ""
        }.addOnFailureListener {
            status.value = it.message.toString()
            status.value = ""
        }
    }

    fun getDoctorDetails(docId: Long): UserModel {
        var model: UserModel

        Realm.getDefaultInstance().use { mRealm ->
            Timber.d("Doctor Details Fetching instance")
            val result = mRealm.where(UserModel::class.java).equalTo("id", docId).findFirst()
            model = mRealm.copyFromRealm(result) as UserModel
            Timber.d("Open Instance att %s", System.currentTimeMillis().toString())
        }

        return model
    }

    // chat functionality

    fun fetchChatRooms(userId: Long) {
        val start = System.currentTimeMillis()
        database.collection("Rooms")
            .whereArrayContains("user_ids_participants", userId)
            .addSnapshotListener { snapshot, e ->
                val roomList = ArrayList<RoomModel>()

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

//                            val msgStsList: RealmList<MessageModel> = RealmList()
//                            val mgStList: ArrayList<Map<String, String>> = lMsgMap.get("list_message_status") as ArrayList<Map<String, String>>
//                            lastMsg.list_message_status =

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
                        Timber.d("room added to realm %s", roomList.toString())
                    }
                    Timber.d("Open Instance at %s", System.currentTimeMillis().toString())
                }
                val end = System.currentTimeMillis()
                Timber.d("Time Taken %s", (end - start).toString())
            }
    }

    fun createChatRoom(model: RoomModel, senderId: Long, receiverId: Long) {
        database.collection("Rooms").whereEqualTo("created_by_id", senderId)
            .whereArrayContains("user_ids_participants", receiverId).get()
            .addOnSuccessListener {
//                for (result in it.documents) {
//                    val array: List<Map<String, String>> =
//                        result.get("list_participants") as List<Map<String, String>>
//                    for (participant in array) {
//                        if ((participant["user_id"].toString() == receiverId.toString()
//                                    && participant["added_by_id"].toString() == senderId.toString())
//                            || (participant["user_id"].toString() == senderId.toString()
//                                    && participant["added_by_id"].toString() == receiverId.toString())
//                        ) {
//                            return@addOnSuccessListener
//                        }
//                    }
//                }
                if (it.isEmpty) {
                    database.collection("Rooms").document(model.room_id.toString()).set(model)
                        .addOnFailureListener { error ->
                            status.value = error.message
                            status.value = ""
                        }
                    Timber.d("room added to firebase")
                }
            }.addOnFailureListener { e ->
                status.value = e.message
                status.value = ""
            }
    }

    fun sendMessage(msgModel: MessageModel) {
        database.collection("Messages").document(msgModel.message_id.toString()).set(msgModel)
            .addOnSuccessListener {
                Timber.d("Msg sent")
            }.addOnFailureListener { _ ->
                status.value = "Sending Failed"
                status.value = ""
            }
    }

    fun fetchMessages(roomId: Long) {
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

    fun fetchSpFromFB() {
        database.collection("Specialists").get().addOnSuccessListener { document ->
            val list = ArrayList<SpecialistModel>()
            for (doc in document.documents) {
                val model = SpecialistModel()
                model.code = doc.get("code").toString()
                model.color_code = doc.get("color_code").toString()
                model.id = doc.get("id").toString().toInt()
                model.image = doc.get("image").toString()
                model.is_deleted = doc.get("is_deleted").toString().toBoolean()
                model.name = doc.get("name").toString()
                model.updated_at = doc.get("updated_at").toString().toLong()

                list.add(model)
            }
            Realm.getDefaultInstance().use { mRealm ->
                mRealm.executeTransaction {
                    it.insertOrUpdate(list)
                }
            }
        }
    }
}