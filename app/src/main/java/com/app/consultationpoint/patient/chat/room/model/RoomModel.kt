package com.app.consultationpoint.patient.chat.room.model

import com.app.consultationpoint.patient.chat.message.model.MessageModel
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RoomModel: RealmObject() {
    @PrimaryKey
    var room_id: Long = 0
    var room_type: Int = 0 // 1-message, 2-group 3-appt, 4-call, 5-broadcast
    var created_by_id: Long = 0

    var photo: String = ""
    var name: String = ""
    var list_participants = RealmList<ParticipantModel>()

    var last_message: MessageModel? = null
    var updated_at: Long = 0
    var created_at: Long = 0

    var is_req_accept_block: Int = 0 //0-Send Req, 1-Accept, 2-Reject, 3-Block

    var is_deleted: Boolean = false
}