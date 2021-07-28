package com.app.consultationpoint.patient.appointment.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class AppointmentModel: RealmObject() {
    @PrimaryKey
    var booking_id: String = ""
    var doc_id: String = ""
    var patient_id: String = ""
    var year_month: String = ""
    var schedual_date: Date? = null
    var schedual_time: String = ""
    var appointmentTitle: String = ""
    var appointmentDesc: String = ""
}