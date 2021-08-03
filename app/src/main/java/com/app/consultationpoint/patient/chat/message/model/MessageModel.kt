package com.app.consultationpoint.patient.chat.message.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MessageModel: RealmObject() {
    @PrimaryKey
    var message_id: Long = 0
    var room_id: Long = 0
    var sender_id: Long = 0

    var content: String = ""
    var content_url: String = "" // file path
    var content_type: Int = 0 // 0-text, 1-attachment, 2-info msg
    var status: Int = 0 // 0-offline, 1-send, 2-Received, 3-read

    var created_at: Long = 0
    var updated_at: Long = 0
    var is_deleted: Boolean = false

    var list_message_status = RealmList<MessageStatusModel>()
}