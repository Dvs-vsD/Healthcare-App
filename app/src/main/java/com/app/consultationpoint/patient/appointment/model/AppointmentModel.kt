package com.app.consultationpoint.patient.appointment.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class AppointmentModel: RealmObject() {
    @PrimaryKey
    var appointment_id: Long = 0
    var created_by: Long = 0

    var patient_id: Long = 0
    var doctor_id: Long = 0

    var title: String = ""
    var note: String = ""

    var schedual_date: String = ""
    var schedual_time: String = ""
    var is_schedual: String = ""
    var type_id: Int = 0
    var status_id: Int = 0
    var status_note: String = ""
    var is_deleted: Boolean = false
    var created_at: Long = 0
    var updated_at: Long = 0
}