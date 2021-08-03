package com.app.consultationpoint.patient.chat.room.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ParticipantModel: RealmObject() {
    @PrimaryKey
    var paticipant_id: Long = 0
    var room_id: Long = 0
    var user_id: Long = 0
    var added_by_id: Long = 0
    var updated_at: Long = 0
    var is_deleted: Boolean = false
}