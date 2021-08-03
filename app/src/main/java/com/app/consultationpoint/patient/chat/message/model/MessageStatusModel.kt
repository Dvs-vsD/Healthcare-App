package com.app.consultationpoint.patient.chat.message.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MessageStatusModel: RealmObject() {
    @PrimaryKey
    var message_status_id: Long = 0
    var message_id: Long = 0
    var user_id: Long = 0
    var status: Int = 0 // 0-offline, 1-send, 2-read
    var receive_read_at: Long = 0
    var updated_at: Long = 0
    var is_deleted: Boolean = false
}