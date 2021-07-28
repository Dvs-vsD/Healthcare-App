package com.app.consultationpoint.patient.doctor.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DoctorModel : RealmObject() {
    @PrimaryKey
    var doc_id: String = ""
    var first_name: String = ""
    var last_name: String = ""
    var profile: String = ""
    var specialization: String = ""
    var city: String = ""
}